import extensions.CSVFile;

class Jeu extends Program {
    // TODO: Faire des tests

    final String[] THEME = new String[] {"histoire", "français", "géographie"};

    final int IDX_QUESTION = 0;
    final int IDX_REPONSE_A = 1;
    final int IDX_REPONSE_B = 2;
    final int IDX_REPONSE_C = 3;
    final int IDX_REPONSE_D = 4;
    final int IDX_SOLUTION_LETTER = 5;
    final int VIES_DEBUT = 3;
    final int NB_QUESTIONS_PAR_TOUR = 3;

    final int NB_COLUMNS_SCORE = 3;

    final String NEW_LINE = "\n";

    // CHARGER LES SCORES 
    String[][] chargerScores() {
        CSVFile scoresCsv = loadCSV("scores.csv");
        String[][] scores = new String[rowCount(scoresCsv)+1][NB_COLUMNS_SCORE];

        for(int l = 0; l < rowCount(scoresCsv); l++) {
            for(int c = 0; c < NB_COLUMNS_SCORE; c++) {
                scores[l][c] = getCell(scoresCsv, l, c);
            }
        }

        return scores;
    }

    // TRIER LES SCORES
    void trierScores(String[][] scores) {
        for (int l = 1; l < length(scores, 1) - 1; l++) {
            String[] current = scores[l];
            int res = stringToInt(current[2]);
            int i = l - 1;

            while (i >= 0 && stringToInt(scores[i][2]) < res) {
                scores[i + 1] = scores[i];
                i = i - 1;
            }

            scores[i + 1] = current;
        }
    }

    // TEST
    void testTrierScores() {
        String[][] scores = new String[][] {
            {"Jean", "cm1", "10"},
            {"Pierre", "cm1", "20"},
            {"Paul", "cm1", "30"},
            null
        };

        trierScores(scores);

        assertEquals("30", scores[0][2]);
        assertEquals("20", scores[1][2]);
        assertEquals("10", scores[2][2]);
    }
    
    // ENREGISTRER LES SCORES 
    void enregistrerScores(Joueur joueur, String[][] scores){
        scores[length(scores, 1)-1][0] = joueur.nom;
        scores[length(scores, 1)-1][1] = joueur.niveau;
        scores[length(scores, 1)-1][2] = joueur.score + "";

        saveCSV(scores, "scores.csv");
    }

    // ENLEVE LES ESPACES D'UNE CHAINE DE CARACTERE
    String enleverEspaces(String chaine) {
        String chaineFinale = "";

        for(int indice = 0; indice < length(chaine); indice++) {
            char caractere = charAt(chaine, indice);
            
            if(caractere != ' ') {
                chaineFinale += caractere;
            }
        }

        return chaineFinale;
    }

    // TEST
    void testEnleverEspaces() {
        assertEquals("test", enleverEspaces(" test"));
        assertEquals("test", enleverEspaces(" te  st "));
    }

    Question[] chargerQuestions(String niveau, String theme) { // charge les questions du fichier csv dans un tableau
        String path =  "questions/" + niveau + "/" + theme + ".csv";

        CSVFile file = loadCSV(path);

        /*un tableau avec pour chaque ligne les questions et les réponses associées*/
            //load le fichier CSV

        Question[] questions = new Question[rowCount(file)];


        for(int idxLigne = 0; idxLigne < length(questions, 1); idxLigne++){

            String question = getCell(file, idxLigne, IDX_QUESTION);
            String reponseA = getCell(file, idxLigne, IDX_REPONSE_A);
            String reponseB = getCell(file, idxLigne, IDX_REPONSE_B);
            String reponseC = getCell(file, idxLigne, IDX_REPONSE_C);
            String reponseD = getCell(file, idxLigne, IDX_REPONSE_D);
            String solution = getCell(file, idxLigne, IDX_SOLUTION_LETTER);

            questions[idxLigne] = newQuestion(question, reponseA, reponseB, reponseC, reponseD, solution);
        }

        return questions;
    } 

    void afficherScore(int vies, int score) {
        String chaine = ANSI_CYAN + "=== VIES : " + ANSI_RED;

        for(int v = 0; v < VIES_DEBUT; v++) {
            if(v >= vies) {
                chaine += "♡";
            } else {
                chaine += "♥";
            }
        }

        String couleurScore = ANSI_RED;

        if(score > 10) {
            couleurScore = ANSI_YELLOW;
        } else if(score > 20) {
            couleurScore = ANSI_GREEN;
        }

        chaine += ANSI_CYAN + " • SCORE : " + ANSI_RED + score + ANSI_CYAN + " ===";

        println(NEW_LINE + chaine + NEW_LINE);
    }

    String toString(Question question) { //affiche la question
        // final String[] question = questions[questionIdx];

        String chaine = ANSI_BOLD + ANSI_YELLOW + question.question + NEW_LINE + ANSI_RESET + ANSI_BOLD +
        "A. " + ANSI_RESET + ANSI_CYAN + question.repA + NEW_LINE + ANSI_RESET + ANSI_BOLD +
        "B. " + ANSI_RESET + ANSI_CYAN + question.repB + NEW_LINE + ANSI_RESET + ANSI_BOLD;

        if(question.repC != null) {
            chaine += "C. " + ANSI_RESET + ANSI_CYAN + question.repC + NEW_LINE + ANSI_RESET + ANSI_BOLD;

            if(question.repD != null) {
                chaine += "D. " + ANSI_RESET + ANSI_CYAN + question.repD + NEW_LINE + ANSI_RESET;
            }
        }

        return chaine;
    }

    String solutionToString(Question question) {
        return ANSI_BLUE + "╚ La réponse était " + question.solution + " (la réponse " + question.lettreSolution + ")" + ".";
    }

    // TRANSFORME UNE LETTRE SOLUTION (A, B, C, D) EN INDICES (0, 1, 2, 3)
    int letterToIndex(char letter) {
        return (int) letter - 64;
    }

    // TRANSFORME UNE LETTRE SOLUTION D'UNE CHAINE DE CARACTERE (A, B, C, D) EN INDICES (1, 2, 3, 4)
    int letterToIndex(String chaine) {
        return letterToIndex(charAt(enleverEspaces(chaine), 0));
    }

    // TEST
    void testLetterToIndex() {
        assertEquals(1, letterToIndex('A'));
        assertEquals(3, letterToIndex('C'));
        assertEquals(1, letterToIndex("A"));
        assertEquals(3, letterToIndex("C"));
    }

    // Compare la réponse du joueur avec la réponse de la question et retourne un boolean
    boolean compare(Question question, String response) {
        String solutionLetterLowerCase = toLowerCase(question.lettreSolution);
        String responseLowerCase = toLowerCase(response);

        String solutionLowerCase = toLowerCase(question.solution);

        return equals(solutionLetterLowerCase, responseLowerCase) || equals(solutionLowerCase, responseLowerCase);
    }

    // TEST
    void testCompare() {
        Question question = newQuestion("Quelle est la capitale de la France ?", "Paris", "Londres", "Berlin", "Madrid", "A");

        assertTrue(compare(question, "Paris"));
        assertTrue(compare(question, "A"));
        assertTrue(compare(question, "a"));
        assertTrue(compare(question, "PARIS"));

        assertFalse(compare(question, "Londres"));
        assertFalse(compare(question, "B"));
        assertFalse(compare(question, "b"));
        assertFalse(compare(question, "LONDRES"));
    }

    // VERIFIE SI L'UTILISATEUR VEUT CONTINUER DE JOUER

    boolean fin(String userInput) {
        if(equals(userInput, "non")){
            return false;
        }
        return true;
    }

    void clearConsole() {
        for(int ligne = 0; ligne < 50; ligne++) {
            println();
        }
    }


    String[] getNiveaux() {
        String[] niveaux = getAllFilesFromDirectory("questions");

        for(int idxNiv = 0; idxNiv < length(niveaux); idxNiv++) {
            if(length(getAllFilesFromDirectory("questions/" + niveaux[idxNiv])) == 0) {
                niveaux[idxNiv] = null;
            }
        }

        return niveaux;
    }

    void afficherNiveaux(String[] niveaux) {
        for(int idxNiv = 0; idxNiv < length(niveaux); idxNiv++) {
            String niveau = niveaux[idxNiv];

            if(niveau != null) {
                println("- au " + toUpperCase(niveau));
            }
        }

        println();
    }

    // VERIFIE SI L'ENTREE UTILISATEUR EST UN NIVEAU (CP, CE1, CE2, CM1, CM2)
    boolean niveauEstCorrect(String[] niveaux, String niveau) {
        boolean correct = false;
        int idxNiv = 0;

        while(idxNiv < length(niveaux) && !correct) {
            if(niveaux[idxNiv] != null && equals(niveaux[idxNiv], niveau)) {
                correct = true;
            }

            idxNiv++;
        }

        return correct;
    }

    // TEST
    void testNiveauEstCorrect() {
        assertTrue(niveauEstCorrect(new String[]{"cp", "ce1"}, "cp"));
        assertFalse(niveauEstCorrect(new String[]{"cp", "ce1", "cm1"}, "cm2"));
    }

    // CREATION DU THEME
    Theme loadTheme(String nom, String niveau) {
        Theme t = new Theme();

        t.nom = nom;
        t.niveau = niveau;
        t.questions = chargerQuestions(niveau, nom);

        return t;
    }
    
    // TEST
    void testLoadTheme() {
        Theme theme = loadTheme("français", "cm1");

        assertTrue(equals(theme.nom, "français"));
    }

    Question newQuestion(String question, String repA, String repB, String repC, String repD, String lettreSolution) {
        Question q = new Question();

        String[] reponses = new String[]{repA, repB, repC, repD};

        q.question = question;
        q.repA = repA;
        q.repB = repB;

        if(!equals(repC, "X")) {
            q.repC = repC;

            if(!equals(repD, "X")) {
                q.repD = repD;
            }
        }

        q.lettreSolution = lettreSolution;
        q.solution = reponses[letterToIndex(lettreSolution)-1];

        return q;
    }

    // TEST
    void testNewQuestion() {
        Question question = newQuestion("Quelle est la capitale de la France ?", "Paris", "Londres", "Berlin", "Madrid", "A");

        assertEquals("Quelle est la capitale de la France ?", question.question);
        assertEquals("Paris", question.repA);
        assertEquals("Londres", question.repB);
        assertEquals("Berlin", question.repC);
        assertEquals("Madrid", question.repD);
        assertEquals("A", question.lettreSolution);
        assertEquals("Paris", question.solution);
    }
    
    Joueur newJoueur(String nom, String niveau){
        Joueur res = new Joueur();
        res.nom = nom;
        res.niveau = niveau;
        return res;
    }

    String toString(Joueur joueur){
        return joueur.nom + " - " + joueur.niveau + " - " + joueur.score;
    } 

    void afficherTableauDesScores() {
        String[][] scores = chargerScores();
        trierScores(scores);

        println();

        int indiceScore = 0;

        println(ANSI_CYAN + "★ --- TABLEAU DES 5 MEILLEURS SCORES --- ★" + NEW_LINE);

        while(indiceScore < length(scores, 1) - 1 && indiceScore < 5) {
            println(ANSI_YELLOW + ANSI_BOLD + (indiceScore + 1) + ". " + ANSI_WHITE + ANSI_CYAN_BG + scores[indiceScore][2] + " points" + ANSI_RESET + ANSI_CYAN + " (" + scores[indiceScore][0] + " • " + scores[indiceScore][1] + ")");
            
            indiceScore++;
        }

        println(NEW_LINE + ANSI_CYAN + "-------------------------------------------");
    }

    void afficherErreurs(Question[] questions) {
        println(ANSI_GREEN + "¡ Un petit récapitulatif de tes erreurs, et des bonnes réponses :");

        println();

        for(int idx = 0; idx < length(questions, 1); idx++) {
            if(questions[idx] != null) {
                println(ANSI_BOLD + ANSI_YELLOW + "• " + questions[idx].question + ANSI_RESET);
                println(solutionToString(questions[idx]));
                println();
            }
        }
    }

    void algorithm() {
        String niveau;
        String[] niveaux = getNiveaux();

        afficherTableauDesScores();

        println();
        print(ANSI_BLUE + "Bienvenue cher apprenti.e, comment t'appelles-tu ? " + ANSI_RESET);

        String nom = readString();
        println();

        do {
            clearConsole();

            println(ANSI_YELLOW + "Parfait, bonjour " + nom + " !" + NEW_LINE + 
            "Dis moi tout... " + "En quelle classe es-tu ?" + NEW_LINE + ANSI_CYAN);
            
            afficherNiveaux(niveaux);

            print(ANSI_BLUE + "Ta classe : " + ANSI_RESET);

            niveau = enleverEspaces(toLowerCase(readString()));
        } while(!niveauEstCorrect(niveaux, niveau));

        Joueur joueur = newJoueur(nom,niveau);
        
        clearConsole();

        boolean fin = false;

        while(!fin && joueur.vies > 0) {
            String userInput;
            int questionIdx = -1;

            String theme;

            do {
                clearConsole();

                println(ANSI_YELLOW + "Quel est le thème de ce tour ?" + NEW_LINE);
                println(ANSI_CYAN + "- histoire" + NEW_LINE + "- français"  + NEW_LINE + "- géographie" + NEW_LINE);
                println("");
                
                print(ANSI_BLUE + "Choix du thème : " + ANSI_RESET);

                theme = enleverEspaces(readString());

                if(equals(theme, "francais")) {
                    theme = "français";
                }

                if(equals(theme, "geo") || equals(theme, "géo") || equals(theme, "geographie")) {
                    theme = "géographie";
                }

            } while(!equals(theme, "français") && !equals(theme, "histoire") && !equals(theme, "géographie") && !equals(theme, "géo") && !equals(theme, "geographie") && !equals(theme, "geo"));

            Theme themeInfos = loadTheme(theme, niveau);

            int cpt = 0;

            clearConsole();

            afficherScore(joueur.vies, joueur.score);

            Question[] erreurs = new Question[NB_QUESTIONS_PAR_TOUR];
            int derniereErreur = -1;

            while(cpt < NB_QUESTIONS_PAR_TOUR && joueur.vies > 0) {
                do {
                    questionIdx = (int) (random() * length(themeInfos.questions, 1));
                } while(themeInfos.questions[questionIdx].passee);

                println(toString(themeInfos.questions[questionIdx]));
                print(ANSI_RESET + ANSI_BLUE + "Entre ta réponse : " + ANSI_RESET);

                themeInfos.questions[questionIdx].passee = true;

                userInput = enleverEspaces(readString());

                clearConsole();

                if(compare(themeInfos.questions[questionIdx], userInput)) {
                    println(ANSI_GREEN + "» Bien joué ! Tu gagnes 10 points !");
                    joueur.score += 10;
                } else {
                    println(ANSI_RED + "» Dommage, tu perds 1 vie ! ");
                    joueur.vies -= 1;
                    derniereErreur += 1;

                    erreurs[derniereErreur] = themeInfos.questions[questionIdx];
                }

                print(solutionToString(themeInfos.questions[questionIdx]) + NEW_LINE);
                afficherScore(joueur.vies, joueur.score);

                cpt++;
            }

            afficherErreurs(erreurs);

            erreurs = new Question[NB_QUESTIONS_PAR_TOUR];

            if(joueur.vies <= 0) {
                println(ANSI_BOLD + ANSI_RED + "Tu as perdu ce tour, tu n'as plus de vies." + NEW_LINE);
            } else {
                println(ANSI_BOLD + ANSI_GREEN + "Félicitations champion ! Tu as gagné ce tour, on en fait un autre ?" + NEW_LINE + ANSI_RESET);
                do {
                    print(ANSI_BLUE + "Souhaites-tu continuer à jouer ? " + ANSI_RESET);
                    userInput = enleverEspaces(readString());
                } while(!equals(userInput, "non") && (!equals(userInput, "oui")));
           
                clearConsole();

                if(!fin(userInput)){
                    fin = true;
                }
            }

        }

        String[][] scores = chargerScores();
        enregistrerScores(joueur, scores);

        if(fin) {
            println(ANSI_CYAN + "Merci d'avoir joué à " + ANSI_YELLOW + ANSI_BOLD + "Questions pour un primaire" + ANSI_RESET + ANSI_CYAN + ", ton score est de " + joueur.score + " points." + NEW_LINE + 
            "À la prochaine !" + NEW_LINE);
        } else {
            println("Tu n'as plus de vie ! La partie est terminé pour toi. Néanmois ton score est de " + joueur.score +" point.s !" + NEW_LINE) ;
        }
    }
}