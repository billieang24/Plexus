# Tasks schema
 
# --- !Ups
CREATE TABLE `plexus`.`task` (
    `id` integer NOT NULL AUTO_INCREMENT,
    `label` varchar(255),
	PRIMARY KEY(id)
);
 
# --- !Downs
 
DROP TABLE `plexus`.`task`;
