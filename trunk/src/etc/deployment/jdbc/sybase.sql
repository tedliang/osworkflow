-- @author Andrew Wilson
-- File for creating OSWorkflow entries for Sybase.

DROP TABLE OS_WFENTRY
DROP TABLE OS_PROPERTYENTRY
DROP TABLE OS_CURRENTSTEP
DROP TABLE OS_HISTORYSTEP
DROP TABLE OS_CURRENTSTEP_PREV
DROP TABLE OS_HISTORYSTEP_PREV
GO


CREATE TABLE OS_PROPERTYENTRY
(
  GLOBAL_KEY varchar(255) ,
  ITEM_KEY varchar(255) ,
  ITEM_TYPE smallint null,
  STRING_VALUE varchar(255) null,
  DATE_VALUE datetime null,
  DATA_VALUE varbinary(2000) null,
  FLOAT_VALUE float null,
  NUMBER_VALUE numeric null,
  primary key (GLOBAL_KEY, ITEM_KEY)
)

create table OS_WFENTRY
(
    ID int,
    NAME varchar(128),
    STATE smallint,
    primary key (ID)
)

create table OS_CURRENTSTEP
(
    ID int,
    ENTRY_ID int,
    STEP_ID smallint,
    ACTION_ID smallint null,
    OWNER varchar(20) null,
    START_DATE datetime,
    FINISH_DATE datetime null,
    DUE_DATE datetime null,
    STATUS varchar(20),
    CALLER varchar(20) null,
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)
)

create table OS_HISTORYSTEP
(
    ID int,
    ENTRY_ID int,
    STEP_ID smallint,
    ACTION_ID smallint,
    OWNER varchar(20) null,
    START_DATE datetime,
    FINISH_DATE datetime,
    DUE_DATE datetime null,
    STATUS varchar(20),
    CALLER varchar(20) null,
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)
)

create table OS_CURRENTSTEP_PREV
(
    ID int,
    PREVIOUS_ID int,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_CURRENTSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
)

create table OS_HISTORYSTEP_PREV
(
    ID int,
    PREVIOUS_ID int,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_HISTORYSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
)
