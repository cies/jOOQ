/*
 * This file is generated by jOOQ.
 */
package org.jooq.example.kotlin.db.h2.tables.pojos


import java.io.Serializable
import java.time.LocalDate


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
data class Author(
    var id: Int? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var dateOfBirth: LocalDate? = null,
    var yearOfBirth: Int? = null,
    var address: String? = null
): Serializable {


    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other === null)
            return false
        if (this::class != other::class)
            return false
        val o: Author = other as Author
        if (this.id === null) {
            if (o.id !== null)
                return false
        }
        else if (this.id != o.id)
            return false
        if (this.firstName === null) {
            if (o.firstName !== null)
                return false
        }
        else if (this.firstName != o.firstName)
            return false
        if (this.lastName === null) {
            if (o.lastName !== null)
                return false
        }
        else if (this.lastName != o.lastName)
            return false
        if (this.dateOfBirth === null) {
            if (o.dateOfBirth !== null)
                return false
        }
        else if (this.dateOfBirth != o.dateOfBirth)
            return false
        if (this.yearOfBirth === null) {
            if (o.yearOfBirth !== null)
                return false
        }
        else if (this.yearOfBirth != o.yearOfBirth)
            return false
        if (this.address === null) {
            if (o.address !== null)
                return false
        }
        else if (this.address != o.address)
            return false
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (if (this.id === null) 0 else this.id.hashCode())
        result = prime * result + (if (this.firstName === null) 0 else this.firstName.hashCode())
        result = prime * result + (if (this.lastName === null) 0 else this.lastName.hashCode())
        result = prime * result + (if (this.dateOfBirth === null) 0 else this.dateOfBirth.hashCode())
        result = prime * result + (if (this.yearOfBirth === null) 0 else this.yearOfBirth.hashCode())
        result = prime * result + (if (this.address === null) 0 else this.address.hashCode())
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("Author (")

        sb.append(id)
        sb.append(", ").append(firstName)
        sb.append(", ").append(lastName)
        sb.append(", ").append(dateOfBirth)
        sb.append(", ").append(yearOfBirth)
        sb.append(", ").append(address)

        sb.append(")")
        return sb.toString()
    }
}
