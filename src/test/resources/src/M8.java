public class M8 {
    private final static String[] f = new String[]
    {
	"It is decidedly so",
	"Without a doubt",
	"Yes definitely",
	"You may rely on it",
	"As I see it, yes",
	"Most likely",
	"Outlook good",
	"Yes",
	"Signs point to yes",
	"Reply hazy, try again",
	"Ask again later",
	"Better not tell you now",
	"Cannot predict now",
	"Concentrate and ask again",
	"Don't count on it",
	"My reply is no",
	"My sources say no",
	"Outlook not so good",
	"Very doubtful"
    };
    public static void main(String[] args) {
	if (args.length == 0) 
		System.out.println("Ask me anything");
	else
        	System.out.println(f[new java.util.Random().nextInt(f.length)]);
    }
}