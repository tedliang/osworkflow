
DROP TABLE IF EXISTS OS_PROPERTYENTRY;
DROP TABLE IF EXISTS OS_CURRENTSTEP_PREV;
DROP TABLE IF EXISTS OS_HISTORYSTEP_PREV;
DROP TABLE IF EXISTS OS_CURRENTSTEP;
DROP TABLE IF EXISTS OS_HISTORYSTEP;
DROP TABLE IF EXISTS OS_WFENTRY;
DROP TABLE IF EXISTS hibernate_sequence;


DROP SEQUENCE SEQ_OS_WFENTRY;
DROP SEQUENCE SEQ_OS_CURRENTSTEPS;

CREATE SEQUENCE SEQ_OS_WFENTRY INCREMENT 2 START 10;
CREATE SEQUENCE SEQ_OS_CURRENTSTEPS INCREMENT 2 START 10;

CREATE TABLE hibernate_sequence(
   id NUMERIC
)

CREATE TABLE OS_WFENTRY
(
    ID int,
    NAME varchar(128),
    STATE smallint,
    primary key (ID)
);

CREATE TABLE OS_CURRENTSTEP
(
    ID int,
    ENTRY_ID int,
    STEP_ID smallint,
    ACTION_ID smallint,
    OWNER varchar(20),
    START_DATE TIMESTAMP ,
    FINISH_DATE TIMESTAMP ,
    DUE_DATE TIMESTAMP ,
    STATUS varchar(20),
    CALLER varchar(20),
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)
   
);

CREATE TABLE OS_HISTORYSTEP
(
    ID int,
    ENTRY_ID int,
    STEP_ID smallint,
    ACTION_ID smallint,
    OWNER varchar(20),
    START_DATE TIMESTAMP ,
    FINISH_DATE TIMESTAMP ,
    DUE_DATE TIMESTAMP ,
    STATUS varchar(20),
    CALLER varchar(20),
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)
);

CREATE TABLE OS_CURRENTSTEP_PREV
(
    ID int,
    PREVIOUS_ID int,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_CURRENTSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

CREATE TABLE OS_HISTORYSTEP_PREV
(
    ID int,
    PREVIOUS_ID int,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_HISTORYSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

create table OS_PROPERTYENTRY (
   entity_name VARCHAR(125) not null,
   entity_id BIGINT not null,
   entity_key VARCHAR(255) not null,
   key_type INTEGER,
   boolean_val BIT,
   double_val DOUBLE,
   string_val VARCHAR(255),
   long_val BIGINT,
   int_val INTEGER,
   date_val DATE,
   primary key (entity_name, entity_id, entity_key)
);



