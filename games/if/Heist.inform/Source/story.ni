"The Crowther Engine Heist" by Kieren Davies

[TODO Fix the errors in the existing file.]
[TODO Provide descriptions where these are missing.]
[TODO Using the existing rooms and objects create a solution to the puzzle of how the Crowther Engine was stolen. In doing so you may add rules and modify object properties and descriptions.]
[TODO Create a time limit (sunrise) before which the puzzle must be solved.]
[TODO Create at least one red herring (set of objects and their interactions that seem like they might solve the puzzle but in the end do not).]

Use no scoring.

When play begins:
	say "As an accomplished detective you are able to mentally simulate how the Crowther Engine must have been stolen. You begin by imagining yourself in the role of the thief.[paragraph break]The thief would have had to complete his mission before Blitzen Crowther returned to the laboratory at sunrise, 6:00 am."

Time of day is 5:30 am.
Every turn:
	if the Brass Man is switched on:
		move the Brass Man to the location;
		move the Crowther Engine to the location;
		say "The Brass Man noisily drags the Crowther Engine along behind you.";
	say "Your pocket watch reads [time of day]."
At 6:00 AM:
	end the game saying "The cock crows with the sunrise, and Crowther's murmuring voice approaches. He turns the corner and bellows, 'You traitor!' He quickly pulls an electric weapon from his coat and fires a fatal shot."

The player carries an Explanatory Note.
	The description of the Explanatory Note is "This hastily penned note reads 'Do not judge me, I am the only one in this nest of vipers with any ethics at all'."

The Shielded Corridor is a room.
	"Outside the Laboratories of the Worthy Order of Curmudgeons, the walls are shielded with sonic damping, fire proofing, acid-resistant lacquer, structural supports and storm baffling. (The last a recent addition inspired by Dr Crowther's experiments). The Cogway, the imposing entrance to the Prime Laboratory, is in the south wall."
	The Lustrous Key is here. The description of the Lustrous Key is "A large and very shiny key."
The Cogway is south of the Shielded Corridor and north of the Prime Laboratory.
	The Cogway is a door.
	The description of the Cogway is "The Affiliates of the Order are jealous of their secrets and this ornate double door constructed entirely from cogs and wheels is one giant locking mechanism."
	The Cogway is locked and lockable.
	The indefinite article of Cogway is "the".
	The Cogway is unlocked by Lustrous Key.

The Prime Laboratory is a room.
	"This massive barrel-vaulted chamber is clustered with half a dozen ongoing experiments. Sparks surge between twisted prongs, sulphurous liquids bubble and murmur, and a half-made automaton rocks itself back and forth in the corner. The hair on the back of your neck rises. This could be from the terrible portent of the experiments conducted here over the centuries or it could be from the actinic charge in the air.[paragraph break]Crowther's Lab is to the west and the Insectarium is to the east. An archway leads south to the flight platform and is also useful for venting in an emergency."
The Rail is here.
	The description of the Rail is "A rail and pulley system that runs across the ceiling so that Dr Peabody's infernal flying machines can be carried to the Launch Platform."
The Daedulus Flutter-wing is on the Rail.
	The Flutter-wing is a vehicle.
	The description of the Flutter-wing is "Peabody's pride and joy but you would have to be suicidal to try and fly in it."
The Slow Glass is here.
	The Slow Glass is fixed in place.
	The description of the Slow Glass is "You are shocked to see Dr Crowther on the other side of the glass, but then you realize that it is only light from earlier this afternoon slowly leaking through."
	The indefinite article of Slow Glass is "the".
The Storm Globes are here.
	The description of the Storm Globes is "Traceries of lightning swirl inside these spheres."
Some Arcane Paraphernalia are here.
	The spider-silk rope, perpetual machine, crank handle and Tesla gun are in the Paraphernalia.
	The Paraphernalia is opaque and openable and closed.
	The description of the Paraphernalia is "The detritus of half a hundred failed experiments. There might be something salvageable in here if your are prepared to risk rummaging."
Before searching Arcane Paraphernalia:
	now the Arcane Paraphernalia are open.
The Beaker is here.
	The Beaker is a container.

The Crowther Lab is a room.
	"The walls of this spherical room are tiled in brass and streaked with soot from Dr Crowther's lightning experiments, but now it is dominated by the Crowther Engine."
	The Crowther Lab is west of the Prime Laboratory.
The Crowther Engine is here.
	The Crowther Engine is a container.
	The description of the Crowther Engine is "This engine on its own can do the work of a hundred calculation drudges in a fraction of the time, but it is far from small and portable."
	The indefinite article of the engine is "the".
	Instead of taking the Crowther Engine:
		say "It's far too heavy for any human to lift, even with prosthetics."
After inserting the Punchcards into the Crowther Engine:
	if the Punchcards are punched:
		now the Brass Man is programmed.
The Punchcards are here.
	The description of the Punchcards is "A small pile of punch cards. With a bit of know how these can be used to program the Crowther Engine."
	The Punchcards can be blank or punched.
	The Punchcards are blank.
After taking the Punchcards:
	if the player has the Punch:
		say "With the procedure prepared, it only takes a minute to punch a program to make an automaton do heavy lifting.";
		now the Punchcards are punched;
	otherwise:
		say "A punch would be useful for these."
The Punch is here.
	The description of the Punch is "A device for punching holes in cards."
After taking the Punch:
	if the player has the Punchcards:
		say "With the procedure prepared, it only takes a minute to punch a program to make an automaton do heavy lifting.";
		now the Punchcards are punched.
The Brass Man is here and a device.
	The description of the Brass Man is "A steam-driven automaton in the shape of a man that is connected by cables to the Crowther Engine. You can see from the boiler panel that it has been allowed to run dry."
	The Brass Man is either dry or fuelled.  The Brass Man is dry.
	The Brass Man is either programmed or unprogrammed.  The Brass Man is unprogrammed.
Before switching on the Brass Man:
	if the Brass Man is dry:
		instead say "With an empty water reservoir the Brass Man has no steam to power it, so it does nothing.";
	if the Brass Man is unprogrammed:
		instead say "The Brass Man turns on momentarily, but with no instructions it powers off again."
The Funnel is here.
	The description of the Funnel is "A funnel with a tube running into the Brass Man's water reservoir."
	The Funnel is a container.
	The Funnel is fixed in place.
After inserting Water into the Funnel:
	now the Brass Man is fuelled;

The Clockwork Insectarium is a room.
	"This room is filled with twisted metal and stained glass constructs in a caricature of a jungle environment. Oversized clockwork beetles, worms, butterflies and spiders move sluggishly among the synthetic foliage. A stained glass doorway leads west back to the main laboratory."
The Clockwork Insectarium is east of the Prime Laboratory.
The clockwork insects are here.
	The description of the clockwork insects is "Each insect is an order of magnitude larger than its biological equivalent, probably because Dr Turk is still having trouble with miniaturisation. They seem to be running low on energy."
	The indefinite article of insects is "the".
The Water is a thing.
	The indefinite article of Water is "some".
After dropping the Storm Globes:
	if the player is in the Clockwork Insectarium:
		say "A thrown Storm Globe shatters, releasing the electric fury of the storm. The clockwork insects all move faster for a moment, and one of them suddenly explodes in a release of shrapnel.  Some of the thick foliage has been blown away to reveal a pool of water.";
		remove the Storm Globes from play;
		move the Water to the Clockwork Insectarium.
Before taking the Water:
	if the player has the Beaker:
		if the player is not in the Crowther Lab: [hacks! I don't know how else to allow putting water into the brass man]
			instead now the Water is in the Beaker;
	otherwise:
		instead say "How do you plan to hold that?"

The Surrounding Forest is a backdrop.
	It is in the Launch Platform, the Tower Base and the Balloon Mooring.
	The description of the Surrounding Forest is "A dense forest stretches towards the horizon. Nearby is the burnt out shell of a village. (After Dr Corvine's invention of fire-rain the villagers decided to rebuild several miles away)."

[Descriptions needed for rooms and things that follow.]
The Launch Platform is a Room.
	"This precarious wooden ramp is where Peabody launches his flying machines, usually to Orvell's detriment. Above is a ladder to a small platform where a hot-air balloon is moored. The tower wall below has a retractable stone staircase down to the ground. Below the tower you can see a donkey cart."
	The Launch Platform is south of the Prime Laboratory.

The Tower Wall is a door.
	"The staircase is retracted and unfortunately the activation mechanism is missing its crank handle."
	The Tower Wall is down from the Launch Platform and up from the Tower Base.
	The Tower Wall is locked and lockable.
	The Tower Wall is unlocked by the Crank Handle.

The Tower Base is a room.
	"From the ground, the tower looms menacingly overhead."
The Donkey Cart is here and a vehicle.
	Instead of entering the Donkey Cart:
		end the game saying "The owner of the donkey cart is a local villager scavenging for valuable debris.  A well-placed blow with a rock and the villager foils the heist, completely unwitting of any of the consequences of this action."

The Balloon Mooring is up from the Launch Platform.
	"Somehow this shaky platform is able to hold a remarkable amount of weight."
The Hot-Air Balloon is here and a vehicle.  The description of the Hot-Air Balloon is "A logo on the basket indicates it was probably illegally acquired from Her Majesty's Balloon Fusiliers Corp."
Instead of entering the Hot-Air Balloon:
	if the Brass Man is switched on:
		if the Explanatory Note is in the Crowther Lab:
			say "After securing the Crowther Engine in the basket, you fly off into the rising sun for your cargo.  Now the disintegration of the Order is certainly imminent.";
			end the game in victory;
		otherwise:
			say "It will not do to escape before planting the explanatory note.";
	otherwise:
		say "It will not do to escape before taking the Crowther Engine."

[Tests start and end in the Prime Laboratory (with few obvious exceptions).]
Test caught with "wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait / wait".
Test sc with "take key / unlock cogway with lustrous key / open cogway / go south".
Test ci with "take storm globes / take beaker / go east / throw storm globes / take water / go west".
Test cl with "go west / drop explanatory note / take punch / take punchcards / put punchcards in crowther engine / put water in funnel /  switch brass man on / go east".
Test main with "test sc / test ci / test cl".
Test escape with "go south / go up / enter balloon".
Test redherring with "search paraphernalia / take crank handle / go south / unlock tower wall with crank handle / go down / enter donkey cart".
Test me with "test main / test escape".