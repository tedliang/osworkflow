drop table if exists OS_PROPERTYENTRY cascade;
create table OS_PROPERTYENTRY
(
	GLOBAL_KEY varchar(250) NOT NULL,
	ITEM_KEY varchar(250) NOT NULL,
	ITEM_TYPE tinyint,
	STRING_VALUE varchar(255),
	DATE_VALUE datetime,
	DATA_VALUE blob,
	FLOAT_VALUE float,
	NUMBER_VALUE numeric,
	primary key (GLOBAL_KEY, ITEM_KEY)
)TYPE=InnoDB;

-- Beginning of Default OSUser tables
drop table if exists OS_USER cascade;
create table OS_USER
(
    USERNAME varchar(100) NOT NULL,
    PASSWORDHASH mediumtext,
    primary key (USERNAME),
    index(USERNAME)
)TYPE=InnoDB;


drop table if exists OS_GROUP cascade;
create table OS_GROUP
(
    GROUPNAME varchar(20) NOT NULL,
    primary key (GROUPNAME)
)TYPE=InnoDB;

drop table if exists OS_MEMBERSHIP cascade;
create table OS_MEMBERSHIP
(
    USERNAME varchar(20) NOT NULL,
    GROUPNAME varchar(20) NOT NULL,
    primary key (USERNAME, GROUPNAME),
    index (USERNAME),
    foreign key (USERNAME) references OS_USER(USERNAME),
    index (GROUPNAME),
    foreign key (GROUPNAME) references OS_GROUP(GROUPNAME)
)TYPE=InnoDB;

-- End of Default OSUser tables



drop table if exists OS_WFENTRY cascade;
create table OS_WFENTRY
(
    ID bigint NOT NULL,
    NAME varchar(60),
    STATE integer,
    primary key (ID)
)TYPE=InnoDB;


drop table if exists OS_CURRENTSTEP;
create table OS_CURRENTSTEP
(
    ID bigint NOT NULL,
    ENTRY_ID bigint,
    STEP_ID integer,
    ACTION_ID integer,
    OWNER varchar(35),
    START_DATE datetime,
    FINISH_DATE datetime,
    DUE_DATE datetime,
    STATUS varchar(40),
	CALLER varchar(35),

    primary key (ID),
    index (ENTRY_ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID),
    index (OWNER),
    foreign key (OWNER) references OS_USER(USERNAME),
    index (CALLER),
    foreign key (CALLER) references OS_USER(USERNAME)
)TYPE=InnoDB;

drop table if exists OS_HISTORYSTEP;
create table OS_HISTORYSTEP
(
    ID bigint NOT NULL,
    ENTRY_ID bigint,
    STEP_ID integer,
    ACTION_ID integer,
    OWNER varchar(35),
    START_DATE datetime,
    FINISH_DATE datetime,
    DUE_DATE datetime,
    STATUS varchar(40),
    CALLER varchar(35),

    primary key (ID),
    index (ENTRY_ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID),
    index (OWNER),
    foreign key (OWNER) references OS_USER(USERNAME),
    index (CALLER),
    foreign key (CALLER) references OS_USER(USERNAME)
)TYPE=InnoDB;

drop table if exists OS_CURRENTSTEP_PREV;
create table OS_CURRENTSTEP_PREV
(
    ID bigint NOT NULL,
    PREVIOUS_ID bigint NOT NULL,
    primary key (ID, PREVIOUS_ID),
    index (ID),
    foreign key (ID) references OS_CURRENTSTEP(ID),
    index (PREVIOUS_ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
)TYPE=InnoDB;

drop table if exists OS_HISTORYSTEP_PREV;
create table OS_HISTORYSTEP_PREV
(
    ID bigint NOT NULL,
    PREVIOUS_ID bigint NOT NULL,
    primary key (ID, PREVIOUS_ID),
    index (ID),
    foreign key (ID) references OS_HISTORYSTEP(ID),
    index (PREVIOUS_ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
)TYPE=InnoDB;

drop table if exists OS_STEPIDS;
CREATE TABLE OS_STEPIDS
(
	 ID bigint NOT NULL AUTO_INCREMENT,
	 PRIMARY KEY (id)
 )TYPE=InnoDB;

drop table if exists OS_ENTRYIDS;
CREATE TABLE OS_ENTRYIDS
(
	 ID bigint NOT NULL AUTO_INCREMENT,
	 PRIMARY KEY (id)
 )TYPE=InnoDB;
