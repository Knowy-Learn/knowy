package com.knowy.persistence.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Cacheable
@Table(name = "language")
public class CategoryEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "name", nullable = false, unique = true, length = 20)
	private String name;

	@ManyToMany(mappedBy = "languages", fetch = FetchType.LAZY)
	private List<PublicUserEntity> publicUsers;

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CategoryEntity that)) return false;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}
}
