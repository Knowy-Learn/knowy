TRUNCATE TABLE public.public_user RESTART IDENTITY CASCADE;
TRUNCATE TABLE public.gender RESTART IDENTITY CASCADE;

INSERT INTO public.gender (name) VALUES('MALE'),('FEMALE'),('OTHER'), ('UNKNOWN');

INSERT INTO public.public_user (nickname, id_gender, id_profile_image) VALUES ('knowyuser', 2, 1); -- id=1
INSERT INTO public.public_user (nickname, id_gender, id_profile_image) VALUES ('knowyuser2', 1, 2);
INSERT INTO public.public_user (nickname, id_gender, id_profile_image) VALUES ('knowyuser3', 3, 2);  -- id=2