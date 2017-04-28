CREATE TABLE IF NOT EXISTS `cmsfs`.`core_cmsfs` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `path` VARCHAR(500) NOT NULL,
  `collect` JSON NOT NULL,
  `analyzes` JSON NULL,
  `alarms` JSON NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_cmsfs_details` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cron` VARCHAR(20) NOT NULL,
  `cmsfs_id` INT NOT NULL,
  `collect` JSON NOT NULL,
  `analyzes` JSON NULL,
  `alarms` JSON NULL,
  INDEX `fk_core_cmsfs_cmsfs_id` (`cmsfs_id` ASC),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_core_task_details_core_tasks`
    FOREIGN KEY (`cmsfs_id`)
    REFERENCES `cmsfs`.`core_cmsfs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_alarm_notify` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `mails` JSON NULL,
  `mobiles` JSON NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

INSERT INTO `cmsfs`.`core_cmsfs` (`id`, `name`, `path`, `collect`, `analyzes`, `alarms`) 
  VALUES (1, 'disk_space', '[\"os\",\"centos\",\"common\",\"disk_space\"]', '{\"mode\":\"collect-ssh-script\",\"files\": [\"collect.sh\"]}', '[{\"files\": [\"analyze.py\"], \"_index\": \"os\", \"_type\": \"disk_space\"}]', '[{\"files\": [\"alarm.py\"]}]');

INSERT INTO `cmsfs`.`core_cmsfs_details` (`id`, `cron`, `cmsfs_id`, `collect`, `analyzes`, `alarms`) 
  VALUES (1, '0 * * * * * ?', 1, '{ \"id\":1, \"args\": \"\"}', '[{\"idx\": 0, \"args\": \"\"}]', '[{\"idx\": 0, \"threshold\": \"\", \"notify\": [1]}]');

INSERT INTO `cmsfs`.`core_alarm_notify` (`id`, `mails`, `mobiles`) 
  VALUES (1, '[\"zhangxu@weibopay.com\"]', '[191631513]');
