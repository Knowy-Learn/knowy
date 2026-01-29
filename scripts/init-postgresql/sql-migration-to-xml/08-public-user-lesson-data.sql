TRUNCATE TABLE public.public_user_lesson RESTART IDENTITY CASCADE;
-- Usuario 1: Curso 1 (lecciones 1-4)
INSERT INTO public.public_user_lesson (id_public_user, id_lesson, start_date, status)
VALUES (1, 1, '2025-06-10', 'completed'),
	   (1, 2, '2025-06-10', 'completed'),
	   (1, 3, '2025-06-10', 'completed'),
	   (1, 4, '2025-06-10', 'completed'),
	   (1, 5, '2025-06-10', 'completed'),
	   (1, 6, '2025-06-10', 'in_progress'),
	   (1, 7, '2025-06-10', 'pending'),
	   (1, 8, '2025-06-10', 'pending'),
	   (1, 9, '2025-06-10', 'pending'),
	   (1, 10, '2025-06-10', 'pending');

-- Usuario 2
INSERT INTO public.public_user_lesson (id_public_user, id_lesson, start_date, status)
VALUES (2, 11, '2025-06-10', 'completed'),
	   (2, 12, '2025-06-10', 'in_progress');

-- Usuario 3
INSERT INTO public.public_user_lesson (id_public_user, id_lesson, start_date, status)
VALUES (2, 13, '2025-06-10', 'completed'),
	   (2, 14, '2025-06-10', 'completed');