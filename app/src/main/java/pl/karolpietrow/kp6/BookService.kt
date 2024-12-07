package pl.karolpietrow.kp6

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class BookService : Service() {
    val books = listOf(
        mapOf("id" to 0, "title" to "Romeo and Juliet", "content" to "\"Romeo and Juliet\" by William Shakespeare is a tragedy likely written during the late 16th century. The play centers on the intense love affair between two young lovers, Romeo Montague and Juliet Capulet, whose families are embroiled in a bitter feud. Their love, while passionate and profound, is met with adversities that ultimately lead to tragic consequences. At the start of the play, a Prologue delivered by the Chorus sets the stage for the tale of forbidden love, revealing the familial conflict that surrounds Romeo and Juliet. The opening scenes depict a public brawl ignited by the feud between the Montagues and Capulets, showcasing the hostility that envelops their lives. As we are introduced to various characters such as Benvolio, Tybalt, and Mercutio, we learn of Romeo's unrequited love for Rosaline. However, this quickly changes when Romeo encounters Juliet at the Capulet ball, where they share a famous and romantic exchange, unwittingly falling in love with each other despite their families' bitter enmity. This initial encounter foreshadows the obstacles they will face as their love story unfolds amidst chaos and conflict."),
        mapOf("id" to 1, "title" to "The Hobbit, or There and Back Again", "content" to "The Hobbit, or There and Back Again is a children's fantasy novel by the English author J. R. R. Tolkien. It was published in 1937 to wide critical acclaim, being nominated for the Carnegie Medal and awarded a prize from the New York Herald Tribune for best juvenile fiction. The book is recognized as a classic in children's literature and is one of the best-selling books of all time, with over 100 million copies sold."),
        mapOf("id" to 2, "title" to "1984", "content" to "Nineteen Eighty-Four (also published as 1984) is a dystopian novel and cautionary tale by English writer Eric Arthur Blair, who wrote under the pen name George Orwell. It was published on 8 June 1949 by Secker & Warburg as Orwell's ninth and final book completed in his lifetime. Thematically, it centres on the consequences of totalitarianism, mass surveillance, and repressive regimentation of people and behaviours within society. Orwell, a staunch believer in democratic socialism and member of the anti-Stalinist Left, modelled the Britain under authoritarian socialism in the novel on the Soviet Union in the era of Stalinism and on the very similar practices of both censorship and propaganda in Nazi Germany. More broadly, the novel examines the role of truth and facts within societies and the ways in which they can be manipulated."),
        mapOf("id" to 3, "title" to "Harry Potter and the Philosopher's Stone", "content" to "Harry Potter and the Philosopher's Stone is a fantasy novel written by the British author J. K. Rowling. It is the first novel in the Harry Potter series and was Rowling's debut novel. It follows Harry Potter, a young wizard who discovers his magical heritage on his eleventh birthday when he receives a letter of acceptance to Hogwarts School of Witchcraft and Wizardry. Harry makes close friends and a few enemies during his first year at the school. With the help of his friends, Ron Weasley and Hermione Granger, he faces an attempted comeback by the dark wizard Lord Voldemort, who killed Harry's parents but failed to kill Harry when he was just 15 months old."),
        mapOf("id" to 4, "title" to "Alice's Adventures in Wonderland", "content" to "\"Alice's Adventures in Wonderland\" by Lewis Carroll is a classic children's novel written in the mid-19th century. The story follows a young girl named Alice who, feeling bored and sleepy while sitting by a riverbank, encounters a White Rabbit and follows it down a rabbit hole, plunging into a fantastical world filled with curious creatures and whimsical adventures. The opening of the book introduces Alice as she daydreams about her surroundings before spotting the White Rabbit, who is both flustered and animated. Curious, Alice pursues the Rabbit and finds herself tumbling down a deep rabbit hole, leading to a curious hall filled with doors, all locked. After experiencing a series of bizarre changes in size from eating and drinking mysterious substances, she begins exploring this new world, initially frustrated by her newfound challenges as she navigates her size and the peculiar inhabitants she meets. The narrative sets the tone for Alice's whimsical and often nonsensical adventures that characterize the entire tale."),
        mapOf("id" to 5, "title" to "The Odyssey", "content" to "\"The Odyssey\" by Homer is an epic poem attributed to the ancient Greek poet, believed to have been composed in the late 8th century BC. This foundational work of Western literature chronicles the adventures of Odysseus, a clever hero whose journey home following the Trojan War is fraught with peril, delays, and divine intervention. The central narrative follows Odysseus' attempts to return to his wife, Penelope, and son, Telemachus, while grappling with the challenges posed by suitors in his absence. The opening portion of \"The Odyssey\" sets the stage for the epic tale by introducing the plight of its hero, Odysseus, who is trapped on the island of Ogygia by the goddess Calypso as he longs to return to Ithaca. The narrative begins with a divine council at Olympus, where the gods discuss Odysseus's fate, revealing their sympathy for him, especially from Athena. It quickly shifts to Ithaca, where Telemachus grapples with his father's absence and the disrespectful suitors devouring his household. Prompted by Athena, he resolves to seek news of Odysseus, embarking on a quest that propels him into a broader world of heroism, fate, and familial loyalty.")
    )

    companion object {
        const val CHANNEL_ID = "download"
        const val CHANNEL_NAME = "Download channel"
        const val CHANNEL_DESCRIPTION = "Download status notifications."
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Pomijam sprawdzenie wersji Androida, ponieważ minimalne API dla projektu jest ustawione na 26
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun showNotification(context: Context, bookId: Int, bookTitle: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ikona powiadomienia
            .setContentTitle("Pobieranie ukończone")
            .setContentText("Książka \"$bookTitle\" została pobrana.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bookId, builder)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        val bookToDownload = books[books.indices.random()]
//        val bookToDownload = books[0]
        download(bookToDownload)

        return START_STICKY
    }

    private fun download(book: Map<String, Any>) {
        Toast.makeText(this, "Rozpoczęto pobieranie...", Toast.LENGTH_SHORT).show()
        val id = book["id"] as Int
        val title = book["title"] as String
        val content = book["content"] as String

        Thread.sleep(Random.nextLong(1000, 3000))
        val wordCount = content.split("\\s+".toRegex()).size
        val charCount = content.replace("\\s+".toRegex(), "").length
        val mostCommonWord = content.split("\\s+".toRegex())
            .groupingBy { it.lowercase() }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "Brak"


        val broadcastIntent = Intent("pl.karolpietrow.DATA_DOWNLOADED").apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("content", content)
            putExtra("wordCount", wordCount)
            putExtra("charCount", charCount)
            putExtra("mostCommonWord", mostCommonWord)
        }
        sendBroadcast(broadcastIntent)
        showNotification(this, id, title)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}