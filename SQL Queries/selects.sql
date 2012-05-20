Use minfootball;

-- SELECT STATEMENTS
SELECT r.startTime,r.endTime
FROM reserved r
INNER JOIN playGround p
  ON p.id = r.playGroundId
WHERE r.startTime LIKE (CONCAT(CURDATE(),'%')) and p.id='2'

/*SELECT *
FROM company c
INNER JOIN  playGround p
    ON p.companyId = c.id
WHERE p.id = '2'*/

/*SELECT *
FROM minifootball.playGround p
WHERE p.city = 'Blagoevgrad';*/
