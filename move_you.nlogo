extensions [time]


breed [humans human]         ;; Pour les humains
breed [exit-doors exit-door] ;; Pour les portes-sorties
breed [buttons button]       ;; Pour les boutons

globals [my-image
  chrono-start   ;; L'heure de départ du chrono
  chrono         ;; Le temps écoulé en secondes
  color-red
  color-green
]



to setup
  clear-all

  ;; Importer l'image
  import-pcolors "rdc.bmp"
  set color-red [208 46 38]
  set color-green [26 128 65]

  ;; Créer les humains
  let humain-compteur 0
  create-humans Nb_Humain [
    set xcor random-xcor
    set ycor random-ycor
    set size 15
    set color blue
    set shape "person"
    set label ""  ;; Cache le label
    set humain-compteur (humain-compteur + 1)
  ]

  ;; Liste des coordonnées
  let portes-coords [[10 20] [20 20] [30 30] [40 40]]
  let boutons-coords [[-10 -10] [-20 -20] [-30 -30] [-40 -40]]

  ;; Créer les portes-sorties
  let porte-compteur 0
  create-exit-doors Nb_Portes [
    if porte-compteur < length portes-coords [
      let coord item porte-compteur portes-coords
      setxy first coord last coord
      set size 10
      set color green
      set shape "flag"
      set label ""  ;; Cache le label
      set porte-compteur (porte-compteur + 1)
    ]
  ]

  ;; Créer les boutons
  let bouton-compteur 0
  create-buttons Nb_Boutons [
    if bouton-compteur < length boutons-coords [
      let coord item bouton-compteur boutons-coords
      setxy first coord last coord
      set size 10
      set color red
      set shape "circle"
      set label ""  ;; Cache le label
      set bouton-compteur (bouton-compteur + 1)
    ]
  ]
  set chrono 0
  reset-ticks
end


to go
  ;; Déplacement des humains
  ask humans [
    move-randomly
  ]

  ;; Chrono
  if (chrono-start = 0) [
    set chrono-start ticks
  ]
  set chrono (round ((ticks - chrono-start) / 10))
  tick
end


to move-randomly
  ; Déplacer la tortue dans une direction aléatoire

  ; Récupérer la couleur du patch devant la tortue
  let current-color [pcolor] of patch-ahead 1  ; Récupérer la couleur du patch devant

  ; Afficher la couleur du patch (utile pour le débogage)
  show (word "Patch color: " current-color)    ; Afficher la couleur du patch

  ; Vérifier si la couleur du patch est dans l'intervalle du rouge (par exemple, entre 0 et 20)
  if current-color >= 10 and current-color <= 30 [
    ; Si la couleur est dans l'intervalle rouge, changer de direction
    set heading random 360  ; Changer de direction
  ]

  while [[pcolor] of patch-right-and-ahead 5 1 >= 10 and [pcolor] of patch-right-and-ahead 5 1 <= 30 ] [
    set heading random 360  ; Change direction randomly
  ]

  while [[pcolor] of patch-left-and-ahead 5 1 >= 10 and [pcolor] of patch-left-and-ahead 5 1 <= 30 ] [
   set heading random 360  ; Change direction randomly
  ]

  ; Vérifier si la couleur du patch est dans l'intervalle du vert (par exemple, entre 50 et 70)
  if current-color >= 40 and current-color <= 70 [
    ; Si la couleur est dans l'intervalle vert, arrêter la tortue
    stop  ; Arrêter la tortue si elle touche du vert
  ]
  ;; Vérifie si l'humain touche une porte-sortie ou un bouton
  if any? exit-doors-here [
    die  ;; L'humain est "sauvé" et disparaît
  ]

  if any? buttons-here [
    set label "ALERTE !"  ;; Déclencher une alarme
  ]
  ; Avancer la tortue d'un pas dans la direction actuelle
  forward Vitesse_Humain
end
@#$#@#$#@
GRAPHICS-WINDOW
219
10
729
521
-1
-1
2.0
1
10
1
1
1
0
1
1
1
-125
125
-125
125
0
0
1
ticks
30.0

BUTTON
25
145
91
178
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
125
145
188
178
NIL
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SLIDER
28
30
200
63
Nb_Humain
Nb_Humain
1
200
5.0
1
1
NIL
HORIZONTAL

SLIDER
24
82
196
115
Vitesse_Humain
Vitesse_Humain
0
2
0.4
0.2
1
NIL
HORIZONTAL

PLOT
756
15
1328
248
Population
Temps
Personnes
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"Morts" 1.0 0 -2674135 true "" "plot count turtles with [ label =\"mort\" ]"
"Vivants" 1.0 0 -1184463 true "" "plot count turtles with [ label =\"vivant\" ]"
"Évacués" 1.0 0 -11085214 true "" "plot count turtles with [ label =\"évacués\" ]"

MONITOR
759
278
931
323
Durée simulation (secondes)
chrono
17
1
11

SLIDER
22
198
194
231
Nb_Portes
Nb_Portes
0
2
1.0
1
1
NIL
HORIZONTAL

SLIDER
32
250
204
283
Nb_Boutons
Nb_Boutons
0
7
3.0
1
1
NIL
HORIZONTAL

@#$#@#$#@
## WHAT IS IT?
ON CHANGE EN FRANCAIS LE TITRE QUAND CEST FINI UNE PARTIE
(à Valider)

Simul'Mans est un projet Netlogo qui est une simulation multi-agents d'une évacuation du bâtiment IC2. Notre mission principale sera de faire une simulation d'évacuation d'un incendie dans le bâtiment.


Pour la simulation, les humains (agent personne) sont représentés par des petits bonhommes de toutes les couleurs. Les foyers de flammes en orange (partant du principe qu'en intérieur la propagation du feu n'est pas (mot pour rapide mais plus scientifique). De la fumée de plus en plus sombre symbole de sa densité se dégage des flammes et se propage dans le bâtiment.
L'enceinte du bâtiment est délimité distinctement avec les murs et portes le composant. 


## HOW IT WORKS

Pour initialiser la simulation on charge un fichier contenant l'enceinte du bâtiment que nous avons modéliser à partir des plans du bâtiment. On se sert de (Façon de charger la map au final).

Comportements : 

Agent personne : 
Une personne est un agent qui se déplace dans un environnement restreint. Son but est d’atteindre une sortie en restant en vie. Une personne peut être plus ou moins paniquée ce qui va influencer sur la rationalité de ses décisions. 

Agent personne responsable : (on voit si on le fait)
Une personne responsable est semblable à une personne à l’exception qu’elle quittera le bâtiment seulement lorsque tous les individus dont elle est responsable auront évacués les lieux. Cette personne est aussi chargée de l’utilisation des extincteurs.

Agent FuméeSansFeu :
La fumée est un agent qui se propage progressivement avec différents état de "toxicité de l'air ambiant" il détruit un agent personne au simple contact si la toxicité est trop grande. (à voir pour la visibilité) 


## HOW TO USE IT

(Protocole des trucs à cliquer pour savoir comment ça marche)

#1 : Appuyer sur le bouton "Setup" pour charger la carte 

#2 : Choisir à l'aide des sliders les paramètres souhaités pour la simulation

(séparer en étapes toutes les variables que la personne peut modifier)

#3


#? Appuyer sur le bouton "Go" pour lancer la simulation



## THINGS TO NOTICE

Pendant le déroulé de la simulation un suivi est possible sur les indicateurs à côté de la simulation pour consulté le nombre de personne restantes (mortes, vivantes et évacuées).

(à voir pour afficher d'autre choses) 

## THINGS TO TRY

(Les choses qu'on peut faire varier dans la simulation)

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

On peut imaginer traiter d’autres types d’alertes (attaque terroriste, présence de gaz, inondation, écroulement, etc).
Une vue en 3D serait aussi préférable pour observer les différents étages.

(on mettra ce qu'on a pas fait)

## NETLOGO FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

Lors de la conception de la simulation nous avons mis en place l'emploi de :

- blabla
- blabla
- blabla


## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)

Article scientifique sur la simulation de foule en lieu restreint et agents :
https://hal.inrae.fr/hal-02940570/document


Article sur la gestion des foules :
https://www.scienceinschool.org/fr/article/2012/crowding-fr/

Chaîne youtube d'un chercheur dans les comportements de foules :
https://www.youtube.com/@Fouloscopie
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fire
false
0
Polygon -7500403 true true 151 286 134 282 103 282 59 248 40 210 32 157 37 108 68 146 71 109 83 72 111 27 127 55 148 11 167 41 180 112 195 57 217 91 226 126 227 203 256 156 256 201 238 263 213 278 183 281
Polygon -955883 true false 126 284 91 251 85 212 91 168 103 132 118 153 125 181 135 141 151 96 185 161 195 203 193 253 164 286
Polygon -2674135 true false 155 284 172 268 172 243 162 224 148 201 130 233 131 260 135 282

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.4.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
