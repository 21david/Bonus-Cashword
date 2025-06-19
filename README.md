## Description
[Bonus Cashword](https://www.texaslottery.com/export/sites/lottery/Games/Scratch_Offs/details.html_252703484.html) is a lottery ticket game. It requires buying a [physical ticket](https://www.texaslottery.com/export/sites/lottery/Images/scratchoffs/2234_img1.gif) to play. This application fills out the crossword puzzle for you, eliminating the need for manual work.

## Instructions to download and run
<!-- ### Option 1 (Fastest) -->
<!-- Download the .jar file and open it. -->

### Option 2
1. Clone the repository with git. (If you don't have git, see [these](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) instructions.)

    ```bash
    git clone https://github.com/21david/Bonus-Cashword.git
    ```

    a. Alternatively, you can do it without git by copying and pasting the entire [`BonusCashword.java`](BonusCashword.java) file into a new file with the same name and continuing to step 2.

2. Make sure you have a JDK (`java -version` and `javac -version` should print a version, if not, see [these](https://www.freecodecamp.org/news/install-openjdk-free-java-multi-os-guide/?utm_source=chatgpt.com) or [these](https://www.geeksforgeeks.org/download-and-install-java-development-kit-jdk-on-windows-mac-and-linux/?utm_source=chatgpt.com) instructions). From the repository directory, run these commands to compile and run the program.
    
    ```bash
    javac BonusCashword.java
    java BonusCashword
    ```

    a. Alternatively, if you have the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) VS Code extension, you can open the [`BonusCashword.java`](BonusCashword.java) file, navigate to the main method (line 50), and press "Run" right above it, instead of running the two commands above.

## Instructions to use
Scratch the letters on the ticket and input them into the text field at the bottom of the screen, then press enter.
If you won any money, it will display the amount below the crossword!

## Screenshot
![Bonus Cashword](/images/bonus-cashword.png)

## Technologies
- [**Java SE 8**](https://docs.oracle.com/javase/8/docs/api/)
    - [Java Swing package](https://docs.oracle.com/javase/8/docs/api/javax/swing/package-summary.html)
    - [Java AWT package](https://docs.oracle.com/javase/8/docs/api/java/awt/package-summary.html)