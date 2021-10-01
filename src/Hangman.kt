import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

class Hangman {
    // Group words by length (key is length, value is word list)
    private val dictionary: MutableMap<Int, LinkedList<String>> = HashMap()
    private var answer: String? = null

    // What the user typed (should be unique, so use Set interface)
    private val guesses: MutableSet<Char> = HashSet()

    // What the user typed that was in the word
    private val correct: MutableSet<Char> = HashSet()

    /**
     * Construct the dictionary
     */
    fun addWord(word: String) {
        //  if key (word length) doesn't exist
        if (!dictionary.containsKey(word.length)) {
            // create a new key and create a new LinkedList
            dictionary[word.length] = LinkedList()
        }
        // add the word to the LunkedList (method add() is from LinkedList)
        dictionary[word.length]!!.add(word)
    }

    /**
     * Reset the game
     */
    fun reset() {
        guesses.clear()
        correct.clear()
        // Set a random word length (between 2 and 8)
        setLength(Random().nextInt(6) + 2)
    }

    /**
     * Begin the game by setting the word length
     */
    fun setLength(length: Int) {
        // This will return linked list size (user type word length)
        val size = dictionary[length]!!.size

        // This will select random word from the linked list and will be used as an "answer"
        answer = dictionary[length]!![Random().nextInt(size)]
    }

    /**
     * Return an answer
     */
    override fun toString(): String {
        return answer!!
    }

    /**
     * Return set of characters of guesses
     */
    fun getGuesses(): Set<Char> {
        return guesses
    }

    /**
     * User makes a guess. If the character is new, add it to the set.
     * Otherwise return false so the user may guess again
     */
    fun makeGuess(letter: Char): Boolean {
        // Only allow lower-case a-z
        if (letter < 'a' || letter > 'z') {
            return false
        }
        // If we already guessed, don't bother
        if (guesses.contains(letter)) {
            return false
        }
        guesses.add(letter)
        if (answer!!.contains("" + letter)) {
            correct.add(letter)
        }
        return true
    }

    /**
     * What can the player see?
     */
    fun visible(): String {
        // StringBuilder concatenates all guessed letter and "-"
        val b = StringBuilder()
        // Compare each character from the answer with guessed letter
        for (letter in answer!!.toCharArray()) {
            b.append(if (guesses.contains(letter)) letter else '-')
        }
        return b.toString()
    }

    /**
     * Did the player win?
     */
    fun won(): Boolean {
        return answer == visible()
    }

    /**
     * How many guesses remain?
     */
    fun guessesRemaining(): Int {
        return MAX_INCORRECT_GUESSES - (guesses.size - correct.size)
    }

    /**
     * Is the game over?
     */
    val isOver: Boolean
        get() = guessesRemaining() <= 0 || won()

    companion object {
        private const val MAX_INCORRECT_GUESSES = 6
    }

    init {
        // Note that dictionary file has a lot of words, which makes it hard to win the game
        // Warning: I got this file from Internet, so it may contain inappropriate words.
        val file = File("dictionary.txt")
        val reader: FileReader
        try {
            reader = FileReader(file)
            val scanner = Scanner(reader)
            // adding words to HashMap. (key: word length, values: words list)
            while (scanner.hasNextLine()) {
                addWord(scanner.nextLine())
            }
            scanner.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}