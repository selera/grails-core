package org.codehaus.groovy.grails.orm.hibernate

/**
 * @author Graeme Rocher
 * @since 1.0
 *
 * Created: Oct 27, 2008
 */
class SortMappingTests extends AbstractGrailsHibernateTests {

    protected void onSetUp() {
        gcl.parseClass '''

import grails.persistence.*

@Entity
class SortMappingBook {
    String title
    static belongsTo = [author:SortMappingAuthor]

    static mapping = {
        sort title:'desc'
    }
}

@Entity
class SortMappingAuthor {

    String name
    Set unibooks

    static hasMany = [books:SortMappingBook]

    static mapping = {
        sort 'name'
        books sort:'title'
    }
}

@Entity
class SortMappingBook2 {
    String title
    static belongsTo = [author:SortMappingAuthor2]

    static mapping = {
        sort title:'desc'
    }
}

@Entity
class SortMappingAuthor2 {

    String name
    Set unibooks

    static hasMany = [books:SortMappingBook2]

    static mapping = {
        sort 'name'
        books sort:'title', order:"desc"
    }
}
'''
    }

    void testDefaultAssociationSortOrderWithDirection() {
        def authorClass = ga.getDomainClass("SortMappingAuthor2").clazz

        def author = authorClass.newInstance(name:"John")
                                .addToBooks(title:"E")
                                .addToBooks(title:"C")
                                .addToBooks(title:"Z")
                                .addToBooks(title:"A")
                                .addToBooks(title:"K")
                                .save(flush:true)
        assertNotNull author

        session.clear()

        author = authorClass.get(1)
        assertNotNull author
        def books = author.books.toList()
        assertEquals "Z", books[0].title
        assertEquals "K", books[1].title
        assertEquals "E", books[2].title
        assertEquals "C", books[3].title
        assertEquals "A", books[4].title
    }

    void testDefaultSortOrderWithFinder() {
        def authorClass = ga.getDomainClass("SortMappingAuthor").clazz

        assertNotNull authorClass.newInstance(name:"Stephen King").save(flush:true)
        assertNotNull authorClass.newInstance(name:"Lee Child").save(flush:true)
        assertNotNull authorClass.newInstance(name:"James Patterson").save(flush:true)
        assertNotNull authorClass.newInstance(name:"Dean Koontz").save(flush:true)

        session.clear()

        def authors = authorClass.findAllByNameLike("%e%")

        assertEquals "Dean Koontz", authors[0].name
        assertEquals "James Patterson", authors[1].name
        assertEquals "Lee Child", authors[2].name
        assertEquals "Stephen King", authors[3].name
    }

    void testDefaultSortOrder() {
        def authorClass = ga.getDomainClass("SortMappingAuthor").clazz

        assertNotNull authorClass.newInstance(name:"Stephen King").save(flush:true)
        assertNotNull authorClass.newInstance(name:"Lee Child").save(flush:true)
        assertNotNull authorClass.newInstance(name:"James Patterson").save(flush:true)
        assertNotNull authorClass.newInstance(name:"Dean Koontz").save(flush:true)

        session.clear()

        def authors = authorClass.list()
        assertEquals "Dean Koontz", authors[0].name
        assertEquals "James Patterson", authors[1].name
        assertEquals "Lee Child", authors[2].name
        assertEquals "Stephen King", authors[3].name
    }

    void testDefaultSortOrderMapSyntax() {
        def authorClass = ga.getDomainClass("SortMappingAuthor").clazz
        def bookClass = ga.getDomainClass("SortMappingBook").clazz

        def author = authorClass.newInstance(name:"John")
                                .addToBooks(title:"E")
                                .addToBooks(title:"C")
                                .addToBooks(title:"Z")
                                .addToBooks(title:"A")
                                .addToBooks(title:"K")
                                .save(flush:true)

        assertNotNull author

        session.clear()

        def books = bookClass.list()
        assertEquals "Z", books[0].title
        assertEquals "K", books[1].title
        assertEquals "E", books[2].title
        assertEquals "C", books[3].title
        assertEquals "A", books[4].title
    }

    void testSortMapping() {
        def authorClass = ga.getDomainClass("SortMappingAuthor").clazz

        def author = authorClass.newInstance(name:"John")
                                .addToBooks(title:"E")
                                .addToBooks(title:"C")
                                .addToBooks(title:"Z")
                                .addToBooks(title:"A")
                                .addToBooks(title:"K")
                                .save(flush:true)

        assertNotNull author

        session.clear()

        author = authorClass.get(1)
        assertNotNull author
        def books = author.books.toList()
        assertEquals "A", books[0].title
        assertEquals "C", books[1].title
        assertEquals "E", books[2].title
        assertEquals "K", books[3].title
        assertEquals "Z", books[4].title
    }
}
