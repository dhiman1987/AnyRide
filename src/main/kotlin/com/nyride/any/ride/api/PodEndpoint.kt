package com.nyride.any.ride.api

import com.nyride.any.ride.data.Pod
import com.nyride.any.ride.data.PodRepository
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/v1/pod")
class PodEndpoint(val podRepository: PodRepository){

    @GetMapping()
    fun getAll(): Iterable<Pod> = podRepository.findAll();


    @GetMapping("/find-driver")
    fun findDriver(@RequestParam("lat") latitude: String,
                  @RequestParam("long") longitude: String,
                  @RequestParam("d") distance : Double ): Iterable<Pod>{

       return this.podRepository.findByTypeAndStatusAndLocationNear(
                "driver",
                "idle",
                Point(longitude.toDouble(), latitude.toDouble()),
                Distance(distance, Metrics.KILOMETERS)
       )
    }

    @PostMapping("/driver")
    fun addDriver(
            @RequestParam("name") name: String,
            @RequestParam("lat") latitude: String,
            @RequestParam("long") longitude: String):Pod{

        val rider : Pod = Pod(null,name,"driver","idle", GeoJsonPoint(
                longitude.toDouble(), latitude.toDouble()
        ))
        return this.podRepository.save(rider)
    }

    @PostMapping("/rider")
    fun addRider(
            @RequestParam("name") name: String,
            @RequestParam("lat") latitude: String,
            @RequestParam("long") longitude: String):Pod{

        val rider : Pod = Pod(null,name,"rider","idle", GeoJsonPoint(
                longitude.toDouble(), latitude.toDouble()
        ))
        return this.podRepository.save(rider)
    }


    @PutMapping("/driver")
    fun updateDriver(@RequestBody driver:Pod): Pod {
        if(driver.id.isNullOrEmpty()){
            throw Exception("Id missing!")
        }
        val pod = podRepository.findById(driver.id.toString())
        if(pod.isPresent){
            return podRepository.save(driver);
        }
        throw Exception("Id ${driver.id} not found!")
    }

    @PutMapping("/rider")
    fun updateRider(@RequestBody rider:Pod): Pod {
        if(rider.id.isNullOrEmpty()){
            throw Exception("Id missing!")
        }
        val pod = podRepository.findById(rider.id.toString())
        if(pod.isPresent){
            return podRepository.save(rider);
        }
        throw Exception("Id ${rider.id} not found!")
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable("id") id: String): String {
        val pod = podRepository.findById(id)
        if(pod.isPresent){
            podRepository.delete(pod.get())
            return "Pod with $id deleted"
        }
        throw Exception("Pod $id not found!")
    }


}