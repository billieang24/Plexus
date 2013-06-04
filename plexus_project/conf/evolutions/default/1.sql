# Tasks schema
 
# --- !Ups
CREATE TABLE `plexus`.`user` (
    `id` integer NOT NULL AUTO_INCREMENT,
    `username` varchar(255),
    `password` varchar(255),
    PRIMARY KEY(id)
);
 
# --- !Downs
 
DROP TABLE `plexus`.`user`;
