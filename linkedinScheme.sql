-- MySQL Script generated by MySQL Workbench
-- Τρι 17 Ιούλ 2018 07:21:10 μμ EEST
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema linkedin
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema linkedin
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `linkedin` DEFAULT CHARACTER SET utf8 ;
USE `linkedin` ;

-- -----------------------------------------------------
-- Table `linkedin`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`user` (
  `iduser` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `pass` VARCHAR(80) NOT NULL,
  `phone` VARCHAR(20) NULL,
  `role` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`iduser`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`post`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`post` (
  `idpost` INT NOT NULL,
  `author` INT NOT NULL,
  `datetime` DATETIME NOT NULL,
  `visible` VARCHAR(45) NOT NULL DEFAULT 'public',
  PRIMARY KEY (`idpost`, `author`),
  INDEX `fk_post_user_idx` (`author` ASC),
  CONSTRAINT `fk_post_user`
    FOREIGN KEY (`author`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`like`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`like` (
  `idpost` INT NOT NULL,
  `iduser` INT NOT NULL,
  INDEX `fk_like_post1_idx` (`idpost` ASC),
  INDEX `fk_like_user1_idx` (`iduser` ASC),
  PRIMARY KEY (`idpost`, `iduser`),
  CONSTRAINT `fk_like_post1`
    FOREIGN KEY (`idpost`)
    REFERENCES `linkedin`.`post` (`idpost`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_like_user1`
    FOREIGN KEY (`iduser`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`connection`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`connection` (
  `iduser1` INT NOT NULL,
  `iduser2` INT NOT NULL,
  `status` TINYINT(1) NOT NULL,
  PRIMARY KEY (`iduser1`, `iduser2`),
  INDEX `fk_connection_user2_idx` (`iduser2` ASC),
  CONSTRAINT `fk_connection_user1`
    FOREIGN KEY (`iduser1`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_connection_user2`
    FOREIGN KEY (`iduser2`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`skills`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`skills` (
  `iduser` INT NOT NULL,
  `skill` VARCHAR(100) NOT NULL,
  `description` VARCHAR(150) NULL,
  `type` TINYINT(1) NOT NULL,
  `datetime_start` DATETIME NULL,
  `datetime_end` DATETIME NULL,
  `visible` VARCHAR(45) NOT NULL DEFAULT 'public',
  PRIMARY KEY (`iduser`),
  CONSTRAINT `fk_skills_user1`
    FOREIGN KEY (`iduser`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`chat` (
  `idchat` INT NOT NULL,
  PRIMARY KEY (`idchat`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`user_has_chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`user_has_chat` (
  `user_has_chatcol1` INT NOT NULL,
  `iduser` INT NOT NULL,
  `idchat` INT NOT NULL,
  `text` VARCHAR(200) NOT NULL,
  `datetime` TIMESTAMP(6) NOT NULL,
  PRIMARY KEY (`user_has_chatcol1`, `iduser`, `idchat`),
  INDEX `fk_user_has_chat_chat1_idx` (`idchat` ASC),
  INDEX `fk_user_has_chat_user1_idx` (`iduser` ASC),
  CONSTRAINT `fk_user_has_chat_user1`
    FOREIGN KEY (`iduser`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_chat_chat1`
    FOREIGN KEY (`idchat`)
    REFERENCES `linkedin`.`chat` (`idchat`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`job`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`job` (
  `idjobs` INT NOT NULL,
  `idauthor` INT NOT NULL,
  `visible` VARCHAR(45) NOT NULL DEFAULT 'public',
  `state` INT NOT NULL,
  PRIMARY KEY (`idjobs`, `idauthor`),
  INDEX `fk_jobs_user1_idx` (`idauthor` ASC),
  CONSTRAINT `fk_jobs_user1`
    FOREIGN KEY (`idauthor`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`candidate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`candidate` (
  `idjob` INT NOT NULL,
  `iduser` INT NOT NULL,
  PRIMARY KEY (`idjob`, `iduser`),
  INDEX `fk_jobs_has_user_user1_idx` (`iduser` ASC),
  INDEX `fk_jobs_has_user_jobs1_idx` (`idjob` ASC),
  CONSTRAINT `fk_jobs_has_user_jobs1`
    FOREIGN KEY (`idjob`)
    REFERENCES `linkedin`.`job` (`idjobs`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_jobs_has_user_user1`
    FOREIGN KEY (`iduser`)
    REFERENCES `linkedin`.`user` (`iduser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `linkedin`.`desired_skill`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `linkedin`.`desired_skill` (
  `desired_skill` INT NOT NULL,
  `job_idjobs` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`desired_skill`, `job_idjobs`),
  INDEX `fk_table1_job1_idx` (`job_idjobs` ASC),
  CONSTRAINT `fk_table1_job1`
    FOREIGN KEY (`job_idjobs`)
    REFERENCES `linkedin`.`job` (`idjobs`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;