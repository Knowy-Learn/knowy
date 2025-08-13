package com.knowy.server.application.usecase.update.categories;

import com.knowy.server.application.exception.data.inconsistent.KnowyInconsistentDataException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.ports.CategoryRepository;
import com.knowy.server.application.ports.UserRepository;
import com.knowy.server.domain.Category;
import com.knowy.server.domain.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case responsible for updating the categories associated with a user.
 * <p>
 * Validates that all provided categories exist in the system and updates the user's profile with the new set of
 * categories.
 */
public class UserUpdateCategoriesUseCase {

	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;


	/**
	 * Constructs a new {@code UserUpdateCategoriesUseCase} with the specified repositories.
	 *
	 * @param userRepository     Repository for accessing and saving user data.
	 * @param categoryRepository Repository for accessing category data.
	 */
	public UserUpdateCategoriesUseCase(UserRepository userRepository, CategoryRepository categoryRepository) {
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
	}

	/**
	 * Updates the categories assigned to a given user.
	 *
	 * @param userId     The ID of the user whose categories will be updated.
	 * @param categories An array of category names to assign to the user. Must not be {@code null} (use an empty array
	 *                   for no categories).
	 * @throws KnowyInconsistentDataException If any provided category does not exist in the system.
	 */
	public void execute(Integer userId, String[] categories) throws KnowyInconsistentDataException {
		Objects.requireNonNull(
			categories,
			"A not null categories array is required, if no categories are selected use an empty array instead of null"
		);

		User user = findUserByIdOrThrow(userId);

		Set<Category> persistedCategories = categoryRepository.findByNameInIgnoreCase(categories);
		ensureCategoriesArePersisted(categories, persistedCategories);

		User newUser = new User(user.id(), user.nickname(), user.profileImage(), persistedCategories);
		userRepository.save(newUser);
	}

	private User findUserByIdOrThrow(int userId) throws KnowyUserNotFoundException {
		return userRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found with id: " + userId));
	}

	private void ensureCategoriesArePersisted(String[] categories, Set<Category> persistedCategories)
		throws KnowyInconsistentDataException {

		Set<String> categoryNames = persistedCategories.stream()
			.map(Category::name)
			.collect(Collectors.toSet());

		List<String> notFound = Arrays.stream(categories)
			.filter(cat -> !categoryNames.contains(cat))
			.toList();

		if (!notFound.isEmpty()) {
			throw new KnowyInconsistentDataException(
				"The following categories do not exist: " + String.join(", ", notFound)
			);
		}
	}
}
