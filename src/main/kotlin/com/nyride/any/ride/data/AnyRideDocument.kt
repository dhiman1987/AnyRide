package com.nyride.any.ride.data

import org.springframework.data.annotation.Id
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Document(collection = "pods")
data class Pod(@Id var id:String?=null,
               var name: String,
               var type: String,
               var status: String,
               @Indexed(name = "2dsphere")
               var location: GeoJsonPoint)

@Repository
interface PodRepository : MongoRepository<Pod, String>{

    fun findByTypeAndStatusAndLocationNear(type:String,
                                           status: String,
                                           point: Point,
                                           distance: Distance) : Iterable<Pod>
}