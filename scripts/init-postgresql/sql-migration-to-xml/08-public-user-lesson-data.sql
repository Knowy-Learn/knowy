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
	   (2, 12, '2025-06-10', 'in_progress'),
	   -- 2 = 0.5
	   (2, 13, '2025-06-11', 'completed'),
	   (2, 14, '2025-06-12', 'completed'),
	   -- 3 = 1
	   (2, 15, '2025-06-13', 'completed'),
	   (2, 16, '2025-06-14', 'completed'),
	   -- 4 = 1
	   (2, 17, '2025-06-15', 'completed'),
	   (2, 18, '2025-06-16', 'in_progress'),
	   -- 5 = 0.5
	   (2, 19, '2025-06-17', 'pending'),
	   (2, 20, '2025-06-18', 'pending'),
	   -- 6 = 0
	   (2, 21, '2025-06-19', 'pending'),
	   (2, 22, '2025-06-20', 'pending'),
	   -- 7 = 0
	   (2, 23, '2025-06-21', 'pending'),
	   (2, 24, '2025-06-23', 'pending'),
	   -- 8 = 0
	   (2, 25, '2025-06-24', 'pending'),
	   (2, 26, '2025-06-25', 'pending');
	   -- 9 = 0

-- Usuario 3
INSERT INTO public.public_user_lesson (id_public_user, id_lesson, start_date, status)
VALUES (3, 13, '2025-06-10', 'completed'),
	   (3, 14, '2025-06-10', 'completed');