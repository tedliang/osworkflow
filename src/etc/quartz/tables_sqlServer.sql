# thanks to George Papastamatopoulos for submitting this ...

# you shouse enter your DB instance's name on the next line in place of "enter_db_name_here"
use [enter_db_name_here]
GO;


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_job_listeners_qrtz_job_details]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_job_listeners] DROP CONSTRAINT FK_qrtz_job_listeners_qrtz_job_details
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_triggers_qrtz_job_details]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_triggers] DROP CONSTRAINT FK_qrtz_triggers_qrtz_job_details
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_cron_triggers_qrtz_triggers]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_cron_triggers] DROP CONSTRAINT FK_qrtz_cron_triggers_qrtz_triggers
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_simple_triggers_qrtz_triggers]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_simple_triggers] DROP CONSTRAINT FK_qrtz_simple_triggers_qrtz_triggers
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_trigger_listeners_qrtz_triggers]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_trigger_listeners] DROP CONSTRAINT FK_qrtz_trigger_listeners_qrtz_triggers
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_calendars]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_calendars]
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_cron_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_cron_triggers]
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_fired_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_fired_triggers]
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_job_details]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_job_details]
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_job_listeners]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_job_listeners]
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_simple_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_simple_triggers]
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_trigger_listeners]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_trigger_listeners]
GO;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_triggers]
GO;

CREATE TABLE [dbo].[qrtz_calendars] (
  [CALENDAR_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [CALENDAR] [image] NOT NULL 
) ON [PRIMARY]
GO;

CREATE TABLE [dbo].[qrtz_cron_triggers] (
  [TRIGGER_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [CRON_EXPRESSION] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL 
) ON [PRIMARY]
GO;

CREATE TABLE [dbo].[qrtz_fired_triggers] (
  [ENTRY_ID] [varchar] (95) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [INSTANCE_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [FIRED_TIME] [bigint] NOT NULL ,
  [TRIGGER_STATE] [varchar] (16) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL 
) ON [PRIMARY]
GO;

CREATE TABLE [dbo].[qrtz_job_details] (
  [JOB_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [JOB_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [JOB_CLASS_NAME] [varchar] (128) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [IS_DURABLE] [varchar] (1) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [IS_STATEFUL] [varchar] (1) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [REQUESTS_RECOVERY] [varchar] (1) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [JOB_DATA] [image] NULL 
) ON [PRIMARY]
GO;

CREATE TABLE [dbo].[qrtz_job_listeners] (
  [JOB_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [JOB_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [JOB_LISTENER] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL 
) ON [PRIMARY]
GO;

CREATE TABLE [dbo].[qrtz_simple_triggers] (
  [TRIGGER_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [REPEAT_COUNT] [bigint] NOT NULL ,
  [REPEAT_INTERVAL] [bigint] NOT NULL ,
  [TIMES_TRIGGERED] [bigint] NOT NULL 
) ON [PRIMARY]
GO;

CREATE TABLE [dbo].[qrtz_trigger_listeners] (
  [TRIGGER_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_LISTENER] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL 
) ON [PRIMARY]
GO;

CREATE TABLE [dbo].[qrtz_triggers] (
  [TRIGGER_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [JOB_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [JOB_GROUP] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [NEXT_FIRE_TIME] [bigint] NULL ,
  [TRIGGER_STATE] [varchar] (16) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [TRIGGER_TYPE] [varchar] (8) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
  [START_TIME] [bigint] NOT NULL ,
  [END_TIME] [bigint] NULL ,
  [CALENDAR_NAME] [varchar] (80) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
  [MISFIRE_INSTR] [smallint] NULL 
) ON [PRIMARY]
GO;

ALTER TABLE [dbo].[qrtz_calendars] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_calendars] PRIMARY KEY  CLUSTERED 
  (
    [CALENDAR_NAME]
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_cron_triggers] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_cron_triggers] PRIMARY KEY  CLUSTERED 
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_fired_triggers] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_fired_triggers] PRIMARY KEY  CLUSTERED 
  (
    [ENTRY_ID],
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_job_details] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_job_details] PRIMARY KEY  CLUSTERED 
  (
    [JOB_NAME],
    [JOB_GROUP]
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_job_listeners] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_job_listeners] PRIMARY KEY  CLUSTERED 
  (
    [JOB_NAME],
    [JOB_GROUP],
    [JOB_LISTENER]
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_simple_triggers] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_simple_triggers] PRIMARY KEY  CLUSTERED 
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_trigger_listeners] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_trigger_listeners] PRIMARY KEY  CLUSTERED 
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP],
    [TRIGGER_LISTENER]
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_triggers] WITH NOCHECK ADD 
  CONSTRAINT [PK_qrtz_triggers] PRIMARY KEY  CLUSTERED 
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY] 
GO;

ALTER TABLE [dbo].[qrtz_cron_triggers] ADD 
  CONSTRAINT [FK_qrtz_cron_triggers_qrtz_triggers] FOREIGN KEY 
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[qrtz_triggers] (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE 
GO;

ALTER TABLE [dbo].[qrtz_job_listeners] ADD 
  CONSTRAINT [FK_qrtz_job_listeners_qrtz_job_details] FOREIGN KEY 
  (
    [JOB_NAME],
    [JOB_GROUP]
  ) REFERENCES [dbo].[qrtz_job_details] (
    [JOB_NAME],
    [JOB_GROUP]
  ) ON DELETE CASCADE 
GO;

ALTER TABLE [dbo].[qrtz_simple_triggers] ADD 
  CONSTRAINT [FK_qrtz_simple_triggers_qrtz_triggers] FOREIGN KEY 
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[qrtz_triggers] (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE 
GO;

ALTER TABLE [dbo].[qrtz_trigger_listeners] ADD 
  CONSTRAINT [FK_qrtz_trigger_listeners_qrtz_triggers] FOREIGN KEY 
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[qrtz_triggers] (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE 
GO;

ALTER TABLE [dbo].[qrtz_triggers] ADD 
  CONSTRAINT [FK_qrtz_triggers_qrtz_job_details] FOREIGN KEY 
  (
    [JOB_NAME],
    [JOB_GROUP]
  ) REFERENCES [dbo].[qrtz_job_details] (
    [JOB_NAME],
    [JOB_GROUP]
  )
GO;

