package pl.edu.agh.kis.pz1;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Deck;
import pl.edu.agh.kis.pz1.commands.*;
import pl.edu.agh.kis.pz1.player_utils.Hand;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Klasa Game reprezentuje grę karcianą, zarządza jej stanem, graczami i przebiegiem rozgrywki.
 * Implementuje interfejs Runnable, co pozwala na uruchomienie gry w osobnym wątku.
 * Gra obsługuje rundy zakładów i wymiany kart oraz kończy rozgrywkę po wyłonieniu zwycięzcy.
 */
@Data
@NoArgsConstructor
public class Game implements Runnable{
    protected static Map<SocketChannel, Player> playersMap = new HashMap<>();
    protected static Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
    private int gameId;
    private static final int NUMBER_OF_CARDS = 5;
    Deck deck;
    final List<Player> players = new ArrayList<>();
    private int currentMaxBet = 0;
    private int pot = 0;
    private int currentPlayerIndex = 0;
    private String phase = "bet";
    private int numberOfPlayers;
    private int checkCount;
    private boolean won = false;
    private List<Player> winners = new ArrayList<>();
    private int roundNumber = 0;
    private CommandFactory commandFactory;
    private boolean started = false;
    Selector selector;
    private boolean isGameRunning = true;

    /**
     * Konstruktor gry, inicjalizujący jej podstawowe właściwości.
     *
     * @param gameId         Unikalny identyfikator gry.
     * @param numberOfPlayers Liczba graczy biorących udział w grze.
     * @param selector        Selektor obsługujący komunikację sieciową z klientami.
     */
    public Game(int gameId, int numberOfPlayers, Selector selector) {
        this.gameId = gameId;
        this.numberOfPlayers = numberOfPlayers;
        deck = new Deck();
        deck.shuffle(deck.createNewDeck());
        commandFactory = new CommandFactory();
        registerCommands();
        this.selector = selector;
    }

    /**
     * Rozdaje nowy zestaw kart o stałej liczbie dla gracza.
     *
     * @return Lista rozdanych kart.
     */
    public List<Card> dealCards(){
        List<Card> cards = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_CARDS; ++i){
            cards.add(deck.dealCard());
        }
        return cards;
    }

    /**
     * Wykonuje komendę przekazaną przez gracza, analizując jej poprawność i wywołując odpowiednią akcję.
     *
     * @param client Obiekt SocketChannel reprezentujący gracza wykonującego komendę.
     * @param parts  Lista parametrów komendy przesłanej przez gracza.
     */
    public void executeCommand(SocketChannel client, List<String> parts){
        String cmd = parts.get(1).toUpperCase();
        Command command = commandFactory.createCommand(cmd);
        Player player = playersMap.get(client);

        if (command == null) {
            sendMessage(client, "Nie ma takiej komendy. Spróbuj ponownie.");
            return;
        }

        if (!isTurn(player)) {
            sendMessage(client, "To nie jest Twoja tura!");
            return;
        }

        if (!player.isActive()) {
            sendMessage(client, "Spasowałeś w tej rundzie.");
            return;
        }

        String response = command.processCommand(this, player, parts);
        sendMessage(client, response);

        if (command.isSucceeded()) {
            handleSuccessfulCommand(parts);
        }
    }

    /**
     * Obsługuje działania po pomyślnym wykonaniu komendy przez gracza.
     *
     * @param parts Lista parametrów wykonanej komendy.
     */
    void handleSuccessfulCommand(List<String> parts) {
        System.out.println("Pot: " + getPot());
        notifyOtherPlayers(parts);

        if (getPhase().equals("bet") && isBetRoundFinished()) {
            // Zakończenie rundy zakładów
            if (isWon()) {
                // Gracz wygrał
                notifyAllPlayers("Wygrał gracz o ID " + getWinners().get(0).getPlayerId()
                        + ". Z układem: \n" + getWinners().get(0).printCards()+ "Wygrana suma to " + getPot());
                closeGame();
            } else if (getRoundNumber() == 2) {
                // Ostateczna runda, oblicz zwycięzcę
                calculateWinner();
                StringBuilder text = new StringBuilder("Wygrał gracz o ID ");
                for (Player winner : getWinners()) {
                    text.append(winner.getPlayerId()).append(". Z układem: \n").append(winner.printCards());
                    text.append("Wygrana suma to ").append(getPot()/getWinners().size()).append("\n");
                }
                text.append("Gra zakończona\n");
                notifyAllPlayers(text.toString());
                closeGame();
            } else {
                // Zmień rundę na wymianę kart
                changeRound();
                notifyAllPlayers("\nRozpoczęła się runda wymiany kart! \n");
                notifyCurrentPlayer();
            }
        } else if (getPhase().equals("draw") && isDrawRoundFinished()) {
            // Zakończenie rundy wymiany kart
            changeRound();
            notifyAllPlayers("\nRozpoczęła się runda zakładów!");
            notifyCurrentPlayer();
        } else {
            nextTurn();
            notifyCurrentPlayer();
        }
    }

    /**
     * Dodaje nowego gracza do gry.
     *
     * @param player Obiekt gracza, który zostaje dołączony do gry.
     */
    public void addPlayer(Player player) {
        player.setPlayerId(players.size());
        players.add(player);
    }

    /**
     * Pobiera gracza, który aktualnie wykonuje swoją turę.
     *
     * @return Obiekt reprezentujący gracza aktualnie wykonującego ruch.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Sprawdza, czy dany gracz ma aktualnie swoją turę.
     *
     * @param player Obiekt gracza, którego tura jest sprawdzana.
     * @return True, jeśli to tura danego gracza, w przeciwnym razie False.
     */
    public boolean isTurn(Player player) {
        return player.getPlayerId() == currentPlayerIndex;
    }

    /**
     * Przechodzi do kolejnego aktywnego gracza.
     */
    public void nextTurn() {
        do {
            currentPlayerIndex++;
            currentPlayerIndex %= players.size();
        } while (!getCurrentPlayer().isActive());
    }

    /**
     * Oblicza liczbę aktywnych graczy, którzy nadal biorą udział w rozgrywce.
     *
     * @return Liczba aktywnych graczy.
     */
    public int numberOfActivePlayers(){
        int count = 0;
        for(Player player : players){
            if(player.isActive()) count++;
        }
        return count;
    }

    /**
     * Metoda zmienia aktualną rundę gry na przeciwną (z "draw" na "bet" lub z "bet" na "draw") i resetuje stany graczy.
     * Resetowane są m.in. statusy graczy oraz ich bieżące zakłady.
     * Ustawiane są również nowe wartości dla kluczowych zmiennych kontrolujących rundę.
     */
    public void changeRound(){
        if(Objects.equals(phase, "draw")) phase = "bet";
        else phase = "draw";
        for(Player player : players){
            player.setPlayed(false);
            player.setContributedChips(0); // !!!
        }
        checkCount = 0;
        roundNumber++;
        currentMaxBet = 0;
        currentPlayerIndex = 0;
    }

    /**
     * Dodaje gracza do listy zwycięzców.
     *
     * @param player Obiekt gracza, który ma zostać dodany do listy zwycięzców.
     */
    private void addWinner(Player player) {
        winners.add(player);
    }


    /**
     * Sprawdza, czy runda zakładów jest zakończona.
     *
     * @return True, jeśli runda zakładów się zakończyła (np. wszyscy gracze spasowali poza jednym,
     *         wszyscy gracze wyrównali zakłady, lub wszyscy wykonali ruchy w bieżącej rundzie); w przeciwnym razie False.
     */
    public boolean isBetRoundFinished(){
        if(numberOfActivePlayers() == 1){
            // powiadom wszystkich o wygraniu x sumy
            setWon(true);
            for(Player player : players){
                if(player.isActive()) addWinner(player);
            }
            return true;
        } else if(checkCount == numberOfPlayers){
            return true;
        }
        for(Player player : players){
            if(!player.isPlayed()){
                return false;
            }
        }
        for(Player player : players){
            if(player.isActive() && player.getContributedChips() != currentMaxBet){
                return false;
            }
        }
        return true;
    }

    /**
     * Sprawdza, czy runda wymiany kart (draw) jest zakończona.
     *
     * @return True, jeśli wszyscy aktywni gracze wykonali już ruch w rundzie wymiany kart; w przeciwnym razie False.
     */
    public boolean isDrawRoundFinished(){
        for(Player player : players){
            if(player.isActive() && !player.isPlayed()){
                return false;
            }
        }
        return true;
    }

    /**
     * Oblicza zwycięzców gry na podstawie układów kart graczy.
     * W przypadku remisu dodaje więcej niż jednego zwycięzcę do listy.
     */
    public void calculateWinner(){
        for(Player player : players){
            Hand h = new Hand(player.getCards(), player.getPlayerId());
            player.setHand(h);
        }

        List<Hand> hands = players.stream() // posortowane i bez nieaktywnych
                .filter(player -> player.isActive())
                .map(Player::getHand).sorted().toList();
        int i = hands.size() - 1;
        for(Player p : playersMap.values()){

            if(p.getPlayerId() == hands.get(i).getPlayerId()){
                winners.add(p);
            }
        }

        while(i > 0 && hands.get(i).compareTo(hands.get(i-1)) == 0){
            --i;
            for(Player p : playersMap.values()){
                if(p.getPlayerId() == hands.get(i).getPlayerId()){
                    winners.add(p);
                }
            }
        }

    }

    /**
     * Główna pętla gry, sprawdzająca gotowość graczy do rozpoczęcia gry.
     * Po zebraniu odpowiedniej liczby graczy, rozpoczyna rozgrywkę.
     */
    @Override
    public void run() {
        boolean checked = false;
        while (isGameRunning && !checked) {
            if (players.size() >= numberOfPlayers) {
                checked = true;
                startGame();
            } else {
                try {
                    Thread.sleep(900); // Czekanie na graczy
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Rozpoczyna rozgrywkę, wysyłając odpowiednie powiadomienia do graczy oraz rozdając im karty.
     */
    void startGame() {
        synchronized (players) {
            notifyAllPlayers("Gra się rozpoczyna! Liczba graczy: " + players.size() + "\n");
            notifyAllPlayers("Aby grać użyj następującego formatu komendy: '" + gameId + " {ruch} {opcjonalnie:parametry ruchu}'\n");

            // Wyświetlenie kart każdemu graczowi
            for (Player player : players) {
                player.setCards(dealCards());
                sendMessage(player.getChannel(), "Twoje id: " + player.getPlayerId() +". Twoje karty: \n" + player.printCards() + "\n");
            }
            notifyCurrentPlayer();

        }
    }

    private void registerCommands(){
        commandFactory.registerCommand("BET", Bet.class);
        commandFactory.registerCommand("CALL", Call.class);
        commandFactory.registerCommand("CHECK", Check.class);
        commandFactory.registerCommand("EXCHANGE", Exchange.class);
        commandFactory.registerCommand("FOLD", Fold.class);
        commandFactory.registerCommand("RAISE", Raise.class);
        commandFactory.registerCommand("STAND", Stand.class);
        commandFactory.registerCommand("CARDS", Cards.class);
    }

    public void sendMessage(SocketChannel client, String message){
        if (client == null || !client.isOpen()) {
            return;
        }
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        pendingData.get(client).add(buffer);
        client.keyFor(selector).interestOps(SelectionKey.OP_WRITE);

        selector.wakeup();
    }

    void notifyOtherPlayers(List<String> parts){
        StringBuilder message;
        message = new StringBuilder("Gracz " + getCurrentPlayer().getPlayerId() + " wykonał ");
        for(int i = 1; i < parts.size(); i++){
            message.append(parts.get(i)).append(" ");
        }
        for (Map.Entry<SocketChannel, Player> entry : playersMap.entrySet()) {
            SocketChannel ch = entry.getKey();
            Player player = entry.getValue();
            if(player.getGame().getGameId() == gameId && !player.equals(getCurrentPlayer())){
                    sendMessage(ch, message.toString());
            }
        }
    }

    void notifyAllPlayers(String message) {
        for (Map.Entry<SocketChannel, Player> entry : playersMap.entrySet()) {
            SocketChannel client = entry.getKey();
            if(playersMap.get(client).getGame().getGameId() == gameId){
                sendMessage(client, message);
            }
        }
    }

    void notifyCurrentPlayer() {
        Player currentPlayer = getCurrentPlayer();
        SocketChannel currentPlayerChannel = currentPlayer.getChannel();
        sendMessage(currentPlayerChannel, "Twoja tura!");
    }

    void resetPlayers() {
        for (Player player : getPlayers()) {
            player.reset();
        }
        System.out.println("Zresetowano wszystkich graczy.");
    }

    void closeGame() {
        resetPlayers();
        players.clear();
        isGameRunning = false;
    }
}