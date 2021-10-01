import java.io.FileNotFoundException
import java.util.*

object GoodHangmanController {
    private fun view(game: GoodHangman) {
        System.out.format(
            "Guesses remaining: %d\nYou used: %s\nWord: %s\n",
            game.guessesRemaining(),
            game.getGuesses().toString(),
            game.visible()
        )
    }

    fun consoleUI(game: GoodHangman) {
        val scanner = Scanner(System.`in`)
        var letter: Char
        var line: String
        println("Enter a word length: ")
        val length = scanner.nextInt()
        game.setLength(length)
        while (!game.isOver) {
            view(game)
            println("Enter a letter: ")
            while (scanner.nextLine().also { line = it } == "");
            letter = line[0]
            while (!game.makeGuess(letter)) {
                println("Try another letter: ")
                letter = scanner.nextLine()[0]
            }
        }
        if (game.won()) {
            println("Yay, you won! It was: $game")
        } else {
            println("You lost! It was: $game")
        }
    }

    @Throws(FileNotFoundException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val game = GoodHangman()
    consoleUI(game)
    }
}