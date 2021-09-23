import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*


/**
 * Written by Seikyung Jung
 * Warning:
 * You must not post this code online.
 * You must not share this code without permission from the author
 *
 */
class GoodHangman {
	// Group words by length (key is length, value is word list)
	private val dictionary: MutableMap<Int, LinkedList<String>> = HashMap()

	// Initially, the list of words of a given length
	// Subsequently, the words remaining based on the filter
	private var candidates = LinkedList<String>()

	// The key is the pattern (e.g., -ee), the value is the list of matching words (e.g., see, bee)
	private val filter: MutableMap<String?, LinkedList<String>> = HashMap()

	// The pattern
	private var answer: String? = null

	// What the user typed
	private val guesses: MutableSet<Char> = HashSet()

	// What the user typed that was in the word
	private val correct: MutableSet<Char> = HashSet()

	// Construct the dictionary
	private fun addWord(word: String) {
		if (!dictionary.containsKey(word.length)) {
			dictionary[word.length] = LinkedList()
		}
		dictionary[word.length]!!.add(word)
	}

	// Reset the game
	fun reset() {
		guesses.clear()
		correct.clear()
		// Set a random word length (between 2 and 8)
		setLength(Random().nextInt(6) + 2)
	}

	// Begin the game by setting the word length
	// Unlike Hangman, we will not select correct answer since  we will chose pattern later.
	// So initially correct answered word should be "-----" (depends on the length)
	// And will decide candidates here
	fun setLength(length: Int) {
		// Initially, the list of words of a given length
		candidates = dictionary[length]!!
		// Initially word will be all "-"
		answer = ""
		for (i in 0 until length) {
			answer = "$answer-"
		}
	}

	// Return the answer (we may have more than one)
	// Initially, the list of words of a given length
	// Subsequently, the words remaining based on the filter
	override fun toString(): String {
		return candidates.toString()
	}

	// The set of characters the user typed so far
	fun getGuesses(): Set<Char> {
		return guesses
	}

	// Check if user typed letter is in word
	// read character by character from word and compare with typed letter
	// this method is called from pattern. You can embed this into pattern method
	// means that we don't need to create pattern if this word typed already
	fun hasLetter(word: String?, typed: Char): Boolean {
		for (letter in word!!.toCharArray()) {
			if (letter == typed) {
				return true
			}
		}
		return false
	}

	// Given a word, construct a pattern based on what the user typed
	// If the typed character isn't in the word, return null since we don't need to create pattern for this word
	// refer to the method visible(). It is similar
	// This method is called from creatFilter.
	fun pattern(word: String, typed: Char): String? {
		val b = StringBuilder()
		for (letter in word.toCharArray()) {
			b.append(if (guesses.contains(letter) || letter == typed) letter else '-')
		}
		return if (!hasLetter(word, typed)) {
			null // means that we don't need to create pattern for this word
		} else b.toString()
	}

	// Group words by common pattern
	// For example:
	// -ee => fee, see, bee, ...
	// -e- => bed, beg, bet, few, hex, ...
	// read candidates (LinkedList,  need loop),
	// create pattern (above method)
	// add it to filter (HashMap) : use containsKey and put method
	fun createFilter(typed: Char) {
		filter.clear()
		for (word in candidates) {
			val pattern = pattern(word, typed)
				?: continue  // go to the next iteration (read next word)
			// if the pattern doesn't exist, create one
			if (!filter.containsKey(pattern)) {
				filter[pattern] = LinkedList()
			}
			// either pattern exist or not, add the word into the hashmap
			//LinkedList<String> bucket = filter.get(pattern);
			//bucket.add(word);
			filter[pattern]!!.add(word)
		}
	}

	// This method is to Select the pattern with the most words in it
	// read filter (HashMap, for loop), which key has largest size?
	// inside loop, print out filter and update  candidates (LinkedList)
	// now you have new candidates (LinkedList) until user type new letter
	fun choosePattern() {
		var max = 0
		for (pattern in filter.keys) {
			val instances = filter[pattern]!!.size
			if (instances > max) {
				max = instances
				answer = pattern
			}
			println(pattern + " " + filter[pattern]!!.size)
		}
		if (filter.containsKey(answer)) {
			candidates = filter[answer]!!
		}
		println(candidates)
	}

	// User makes a guess. If the character is new, add it to the set.
	// Otherwise return false so the user may guess again
	// You will need to call createFilter() and choosePattern() methods here
	fun makeGuess(letter: Char): Boolean {
		// Only allow lower-case a-z
		if (letter < 'a' || letter > 'z') {
			return false
		}
		// If we already guessed, don't bother
		if (guesses.contains(letter)) {
			return false
		}
		// these two lines are added compared to Hangman
		createFilter(letter)
		choosePattern()
		guesses.add(letter)
		if (answer!!.contains("" + letter)) {
			correct.add(letter)
		}
		return true
	}

	// What can the player see?
	fun visible(): String {
		// StringBuilder concatenates all guessed letter and "-"
		val b = StringBuilder()
		// Compare each character from the answer with guessed letter
		for (letter in answer!!.toCharArray()) {
			b.append(if (guesses.contains(letter)) letter else '-')
		}
		return b.toString()
	}

	// Did the player win?
	fun won(): Boolean {
		return !hasLetter(answer, '-')
	}

	// How many guesses remain?
	fun guessesRemaining(): Int {
		return MAX_INCORRECT_GUESSES - (guesses.size - correct.size)
	}

	// Is the game over?
	val isOver: Boolean
		get() = guessesRemaining() <= 0 || won()

	companion object {
		private const val MAX_INCORRECT_GUESSES = 6
	}

	init {
		// Note that dictionary file has a lot of words, which makes it hard to win the game
		// Warning: I got this file from Internet, so it contains inappropriate words.
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