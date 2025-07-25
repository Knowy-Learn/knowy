CREATE TABLE IF NOT EXISTS public.public_user
(
	id               serial      NOT NULL,
	id_profile_image integer     NOT NULL DEFAULT 1,
	nickname         varchar(50) NOT NULL UNIQUE,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.private_user
(
	id       integer      NOT NULL,
	email    varchar(100) NOT NULL UNIQUE,
	password varchar(100) NOT NULL,
	active	 boolean	  DEFAULT TRUE,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.mission
(
	id             serial      NOT NULL,
	description    varchar(60) NOT NULL,
	total_progress integer     NOT NULL CHECK ( total_progress >= 0 ),
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.public_user_mission
(
	id_public_user   integer NOT NULL,
	id_mission       integer NOT NULL,
	current_progress integer DEFAULT 0 CHECK ( current_progress >= 0 ),
	init_date        date    NOT NULL,
	PRIMARY KEY (id_public_user, id_mission)
);

CREATE TABLE IF NOT EXISTS public.course
(
	id            serial    NOT NULL,
	title         varchar(100),
	description   varchar(250),
	author        varchar(250),
	creation_date timestamp NOT NULL DEFAULT current_timestamp,
	image       text  NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.lesson
(
	id             serial       NOT NULL,
	id_course      integer      NOT NULL,
	id_next_lesson integer,
	title          varchar(100) NOT NULL,
	explanation    text         NOT NULL DEFAULT '',
	PRIMARY KEY (id),
	UNIQUE (id_next_lesson)
);

CREATE TABLE IF NOT EXISTS public.public_user_lesson
(
	id_public_user integer     NOT NULL,
	id_lesson      integer     NOT NULL,
	start_date     date        NOT NULL DEFAULT current_date,
	status         varchar(20) NOT NULL DEFAULT 'pending', -- 'pending' , 'in_progress', 'completed'
	PRIMARY KEY (id_public_user, id_lesson)
);

CREATE TABLE IF NOT EXISTS public.exercise
(
	id        serial  NOT NULL,
	id_lesson integer NOT NULL,
	question  text    NOT NULL DEFAULT '',
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.option
(
	id          serial  NOT NULL,
	id_exercise integer NOT NULL,
	option_text text    NOT NULL DEFAULT '',
	is_correct  boolean NOT NULL DEFAULT false,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.public_user_exercise
(
	id_public_user integer   NOT NULL,
	id_exercise    integer   NOT NULL,
	next_review    timestamp NOT NULL DEFAULT current_timestamp,
	rate           integer   NOT NULL DEFAULT 0,
	PRIMARY KEY (id_public_user, id_exercise)
);

CREATE TABLE IF NOT EXISTS public.profile_image
(
	id  serial NOT NULL,
	url text   NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.banned_word
(
	id   serial      NOT NULL,
	word varchar(50) NOT NULL UNIQUE,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.language
(
	id   serial      NOT NULL,
	name varchar(20) NOT NULL UNIQUE,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.public_user_language
(
	id_public_user integer NOT NULL,
	id_language    integer NOT NULL,
	PRIMARY KEY (id_public_user, id_language)
);

CREATE TABLE IF NOT EXISTS public.course_language
(
	id_course   INTEGER NOT NULL,
	id_language INTEGER NOT NULL,
	PRIMARY KEY (id_course, id_language)
);

CREATE TABLE IF NOT EXISTS public.documentation
(
	id			serial NOT NULL,
	title		varchar(100) NOT NULL,
	link		text NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.lesson_documentation
(
	id_lesson	integer NOT NULL,
	id_documentation integer NOT NULL,
	PRIMARY KEY (id_lesson, id_documentation)
);

-- FK public_user
ALTER TABLE IF EXISTS public.public_user
	ADD FOREIGN KEY (id_profile_image)
		REFERENCES public.profile_image (id);

-- FK private_user
ALTER TABLE IF EXISTS public.private_user
	ADD FOREIGN KEY (id)
		REFERENCES public.public_user (id);

-- FK public_user_mission
ALTER TABLE IF EXISTS public.public_user_mission
	ADD FOREIGN KEY (id_public_user)
		REFERENCES public.public_user (id);

ALTER TABLE IF EXISTS public.public_user_mission
	ADD FOREIGN KEY (id_mission)
		REFERENCES public.mission (id);

-- FK public_user_lesson
ALTER TABLE IF EXISTS public.public_user_lesson
	ADD FOREIGN KEY (id_public_user)
		REFERENCES public.public_user (id);

ALTER TABLE IF EXISTS public.public_user_lesson
	ADD FOREIGN KEY (id_lesson)
		REFERENCES public.lesson (id);

-- FK lesson
ALTER TABLE IF EXISTS public.lesson
	ADD FOREIGN KEY (id_course)
		REFERENCES public.course (id);

ALTER TABLE IF EXISTS public.lesson
	ADD FOREIGN KEY (id_next_lesson)
		REFERENCES public.lesson (id);

-- FK exercise
ALTER TABLE IF EXISTS public.exercise
	ADD FOREIGN KEY (id_lesson)
		REFERENCES public.lesson (id);

-- FK option
ALTER TABLE IF EXISTS public.option
	ADD FOREIGN KEY (id_exercise)
		REFERENCES public.exercise (id);

-- FK public_user_exercise
ALTER TABLE IF EXISTS public.public_user_exercise
	ADD FOREIGN KEY (id_public_user)
		REFERENCES public.public_user (id);

ALTER TABLE IF EXISTS public.public_user_exercise
	ADD FOREIGN KEY (id_exercise)
		REFERENCES public.exercise (id);

--FK public_user_language
ALTER TABLE IF EXISTS public.public_user_language
	ADD FOREIGN KEY (id_public_user)
		REFERENCES public.public_user (id);

ALTER TABLE IF EXISTS public.public_user_language
	ADD FOREIGN KEY (id_language)
		REFERENCES public.language (id);
-- FK course_language
ALTER TABLE IF EXISTS public.course_language
	ADD FOREIGN KEY (id_course)
		REFERENCES public.course (id);

ALTER TABLE IF EXISTS public.course_language
	ADD FOREIGN KEY (id_language)
		REFERENCES public.language (id);

-- FK lesson_documentation
ALTER TABLE IF EXISTS public.lesson_documentation
	ADD FOREIGN KEY (id_lesson)
		REFERENCES public.lesson (id);

ALTER TABLE IF EXISTS public.lesson_documentation
	ADD FOREIGN KEY (id_documentation)
		REFERENCES public.documentation (id);