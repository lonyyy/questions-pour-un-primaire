# questions-pour-un-primaire
Little educational game for a school project. Coded in iJava. 

## French detailed description

Le jeu s’agit d’un questionnaire destiné aux primaires. Le jeu est divisé en différents niveaux : CP, CE1, CE2, CM1 et CM2. Chacun de ces niveaux contient 3 matières : histoire, géographie et français. La difficulté de chaque matière est adaptée en fonction du niveau choisi au démarrage. A la fin de la partie, on obtient un score par rapport au nombre de bonnes réponses que l’on obtient. Et on possède 3 vies à chaque tour. Chaque mauvaise réponse nous enlève 1 vie, quand on n’en a plus, la partie se termine. Étant un jeu pédagogique, il y a un côté apprentissage, qui rappelle à la fin du tour les mauvaises réponses que l’on a pu obtenir, et la correction, pour pouvoir apprendre et s’améliorer.

## Comment jouer ?

Récupérer les fichiers et dossiers du répertoire.

Pour compiler le jeu, utilisez la commande :
```sh
./compile.sh
```

Puis, une fois la commande exécutée, utilisez la commande suivante pour démarrer le jeu :
```sh
./run.sh
```

## Les commandes ne fonctionnent pas ?
Si ces commandes ne marchent pas, il y a un certainement un problème de droits, exécutez :
```sh
chmod u+x compile.sh run.sh
```
Puis utilisez les commandes citées au dessus.
