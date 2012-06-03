DROP DATABASE miniFootball;
CREATE database miniFootball;

USE miniFootball;

CREATE TABLE IF NOT EXISTS company (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  name VARCHAR(20) NOT NULL,
  address VARCHAR(100) NOT NULL,
  email VARCHAR(25) NOT NULL,
  telephone VARCHAR(20) NOT NULL,
  city VARCHAR(25) NOT NULL,
  country VARCHAR(25) NOT NULL,
  UNIQUE(name)
);


CREATE TABLE IF NOT EXISTS playGround (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  companyId INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  address VARCHAR(100) NOT NULL,
  telephone VARCHAR(20) NOT NULL,
  email VARCHAR(25),
  width DOUBLE,
  length DOUBLE,
  flooring VARCHAR(25),
  city VARCHAR(25) NOT NULL,
  country VARCHAR(25) NOT NULL,
  UNIQUE(name),
  FOREIGN KEY(companyId) REFERENCES company(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userProfile (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  userName VARCHAR(15) NOT NULL,
  pass VARCHAR(64) NOT NULL,
  firstName VARCHAR(15) NOT NULL,
  lastName VARCHAR(20) NOT NULL,
  telephone VARCHAR(20),
  address VARCHAR(100),
  email VARCHAR(25) NOT NULL,
  UNIQUE(userName)
);

CREATE TABLE IF NOT EXISTS reserved (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  userId INT NOT NULL,
  playGroundId INT NOT NULL,
  startTime DATETIME NOT NULL,
  endTime DATETIME NOT NULL,
  FOREIGN KEY(userId) REFERENCES userProfile(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY(playGroundId) REFERENCES playGround(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);


-- INSERT VALUES
INSERT INTO company(name,address,email,telephone,city,country)
VALUES('Valdano','str.St. Naum 11','valdano@abv.bg','(+359)882233221','Blagoevgrad','Bulgaria'),
('Ilian92','bul.St. Kiril i Metodii 121','ilian92@abv.bg','(+359)782353221','Blagoevgrad','Bulgaria'),
('Champions','str.St. Fritiof Nansen 21','champ@abv.bg','(+359)882233999','Blagoevgrad','Bulgaria'),
('SofiaSport','str.St. Peter Beron 8','sofSport@gmail.com','(+359)882211621','Sofia','Bulgaria'),
('ExtremeFusball','bul.St. Slibnica 3','xtrmFusball@gmail.com','(+359)882213322','Sofia','Bulgaria');


INSERT INTO playGround(companyId,name,address,telephone,email,width,length,flooring,city,country)
VALUES(1,'MG 1','str. Ar. Kostencev 1','(+359)882233221','valdano@abv.bg',6.5,20,'rubber','Blagoevgrad','Bulgaria'),
(1,'Stroitel 1','str. Fritiof Nansen 2','(+359)882233221','valdano@abv.bg',6.5,20,'rubber','Blagoevgrad','Bulgaria'),
(1,'Ohrid 1','bul. Ohrid 13','(+319)827723233','valdano@yahoo.mk',4,23,'rubber','Ohrid','Macedonia'),
(1,'Belgrad 1','bul. Belgrad 23','(+301)822223233','valdano@yahoo.sr',5,19,'rubber','Blagoevgrad','Serbia'),
(2,'Strumsko 1','str. Dimiter Solunski 3','(+359)882299921','ilian92@abv.bg',5,22,'sand and grass','Blagoevgrad','Bulgaria'),
(2,'Strumsko 2','str. Dimiter Solunski 4','(+359)882299921','ilian92@abv.bg',5,22,'sand and grass','Blagoevgrad','Bulgaria'),
(3,'Elenovo 1','str. Evlogii Georgiev 56','(+359)882243421','champ@abv.bg',6,20,'grass','Blagoevgrad','Bulgaria'),
(4,'Orlandovsti 1','str. Strumica 23 ','(+359)882299333','sofSport@gmail.com',7,25,'grass','Sofia','Bulgaria'),
(5,'Studentski Grad 1','str. St. Kliment Ohridski 2 ','(+359)882277333','xtrmFusball@gmail.com',6,22,'grass','Sofia','Bulgaria');


INSERT INTO userProfile(userName,pass,firstName,lastName,email)
VALUES('soko','�A"!%�d�팃�c���߹��b,�6�','Stanimir','Ivanov','stan79@abv.bg'),
('ruskoff','�A"!%�d�팃�c���߹��b,�6�','Petyr','Ruskoff','ruskoff@abv.bg');


INSERT INTO reserved(userId,playGroundId,startTime,endTime)
VALUES(1,1,'2012-02-22 14-00-00','2012-02-22 15-00-00'),
(1,2,'2012-04-21 14-00-00','2012-04-21 15-00-00'),
(2,4,'2012-03-2 17-00-00','2012-03-2 18-00-00'),
(2,4,'2012-03-21 18-00-00','2012-03-21 19-00-00');