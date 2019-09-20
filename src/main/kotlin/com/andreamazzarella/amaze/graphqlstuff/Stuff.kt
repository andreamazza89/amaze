package com.andreamazzarella.amaze.graphqlstuff

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

@Component
class Stuff() : GraphQLQueryResolver {
    fun recentPosts(): Int {
        return 42
    }
}

