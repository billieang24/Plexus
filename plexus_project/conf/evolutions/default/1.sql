# Tasks schema
 
# --- !Ups
CREATE TABLE `plexus`.`user` (
    `id` integer NOT NULL AUTO_INCREMENT,
    `username` varchar(255),
    `password` varchar(255),
    `givenname` varchar(255),
    `lastname` varchar(255),
    `gender` varchar(255),
    `birthdate` date,
    `address` varchar(255),
    PRIMARY KEY(id)
);
 
# --- !Downs
 
DROP TABLE `plexus`.`user`;
