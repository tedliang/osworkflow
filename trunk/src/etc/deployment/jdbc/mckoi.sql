
CREATE TABLE OS_WFENTRY
(
    ID int,
    NAME varchar(128),
    STATE smallint,
    primary key (ID)
);

CREATE SEQUENCE SEQ_OS_WFENTRY INCREMENT 2 START 10;

CREATE SEQUENCE SEQ_OS_CURRENTSTEPS INCREMENT 2 START 10;
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

CREATE TABLE OS_PROPERTYENTRY 
(
  GLOBAL_KEY varchar(255), 
  ITEM_KEY varchar(255), 
  ITEM_TYPE smallint, 
  STRING_VALUE varchar(255), 
  DATE_VALUE TIMESTAMP , 
  DATA_VALUE varbinary(2000), 
  FLOAT_VALUE float, 
  NUMBER_VALUE numeric, 
  primary key (GLOBAL_KEY, ITEM_KEY)
);



