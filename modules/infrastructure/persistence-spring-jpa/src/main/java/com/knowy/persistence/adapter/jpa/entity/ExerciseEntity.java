package com.knowy.persistence.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exercise")
public class ExerciseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_lesson", nullable = false)
	private LessonEntity lesson;

	@Column(name = "statement", length = 100, nullable = false)
	private String question;

	@OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY)
	private List<OptionEntity> options;
}
