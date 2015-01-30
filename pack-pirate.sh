#!/bin/bash

jarfile=$1
jar uf $jarfile assets/city/kth* 
jar uf $jarfile assets/streetpirates-level*tmx
jar uf $jarfile assets/streetpirates-city*tmx
jar uf $jarfile assets/streetpirates-level*placement*xml
jar uf $jarfile assets/streetpirates-level*scale*png
jar uf $jarfile assets/city/city*ct28* 
jar uf $jarfile assets/city/sea.jpg
jar uf $jarfile assets/city/bandits*png
jar uf $jarfile assets/city/*htta*maurh*png
jar uf $jarfile assets/city/xoros*jpg
jar uf $jarfile assets/city/map*png
jar uf $jarfile assets/pirate/*png 
jar uf $jarfile assets/cars/red*png 
jar uf $jarfile assets/cars/green*png 
jar uf $jarfile assets/cars/blue*png 
jar uf $jarfile assets/menu/*png 
jar uf $jarfile assets/menu/*jpg
jar uf $jarfile assets/map/FOOTPRINTS.png
jar uf $jarfile assets/map/trafficLight.png
jar uf $jarfile assets/map/parrot_front.png
jar uf $jarfile assets/map/parrot_profil.png
jar uf $jarfile assets/map/texts*.png
jar uf $jarfile assets/map/EXIT.png
jar uf $jarfile assets/map/Map*png
jar uf $jarfile assets/map/map*png
jar uf $jarfile assets/map/pirateflag.png
jar uf $jarfile assets/map/treasure1.png
jar uf $jarfile assets/map/compass.png
jar uf $jarfile assets/map/starfish-alpha.png
jar uf $jarfile assets/storytelling/*downsize*jpg
jar uf $jarfile assets/map/nekrotiles2.png
jar uf $jarfile assets/menu/Background_papyrus.jpg
jar uf $jarfile assets/menu/she_parrot.png
jar uf $jarfile assets/menu/she_resize.png
jar uf $jarfile assets/map/ODHGEIES_cropped.png
jar uf $jarfile assets/menu/Background_papyrus.jpg
jar uf $jarfile assets/menu/horn.wav
jar uf $jarfile assets/menu/cheer.wav
jar uf $jarfile assets/menu/intro.wav
jar uf $jarfile assets/menu/city.wav
jar uf $jarfile assets/menu/pirate.wav
jar uf $jarfile assets/menu/Cloudcity.wav
zip $jarfile.zip $jarfile assets/streetpirates-level*-placement.xml assets/menu/*wav
