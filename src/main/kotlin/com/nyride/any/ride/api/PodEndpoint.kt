package com.nyride.any.ride.api

import com.nyride.any.ride.data.Pod
import com.nyride.any.ride.data.PodRepository
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.web.bind.annotation.*
import java.util.*
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin //Adding this so that it works from stackblitz
@RequestMapping("/rest/v1/pod")
class PodEndpoint(val podRepository: PodRepository){

    @GetMapping()
    fun getAll(): Iterable<Pod> = podRepository.findAll();

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id:String): Pod {
        val podOpt: Optional<Pod> = podRepository.findById(id)
        if(!podOpt.isPresent){
            throw Exception("Invalid id $id")
        }
        var pod: Pod = podOpt.get()
        pod.password="XXXXXXXX"
        return pod
    }

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
            @RequestParam("mobile") mobile: String,
            @RequestParam("password") password: String,
            @RequestParam("lat") latitude: String,
            @RequestParam("licenseNumber") licenseNumber: String,
            @RequestParam("long") longitude: String):Pod{

        val rider : Pod = Pod(null,name,mobile,password,"driver","idle", licenseNumber, GeoJsonPoint(
                longitude.toDouble(), latitude.toDouble()
        ))
        return this.podRepository.save(rider)
    }

    @PostMapping("/login")
    fun login( @RequestParam("mobile") mobile: String,
               @RequestParam("password") password: String):Pod{
        val podOpt: Optional<Pod> = this.podRepository.findByMobileAndPassword(mobile, password)
        if(!podOpt.isPresent){
            throw Exception("Invalid login")
        }
        var pod: Pod = podOpt.get()
        pod.password="XXXXXXXX"
        return pod
    }

    @PostMapping("/rider")
    fun addRider(
            @RequestParam("name") name: String,
            @RequestParam("mobile") mobile: String,
            @RequestParam("password") password: String,
            @RequestParam("lat") latitude: String,
            @RequestParam("long") longitude: String):Pod{

        val rider : Pod = Pod(null,name,mobile,password,"rider","idle", null,GeoJsonPoint(
                longitude.toDouble(), latitude.toDouble()))

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

    @PutMapping("/driver-location-status")
    fun updateLocationAndStatus(   @RequestParam("id") id: String,
                                   @RequestParam("status" , required = false) status: String,
                                   @RequestParam("password" , required = false) password: String,
                                   @RequestParam("lat", required = false) latitude: String): Pod {
        if(id.isNullOrEmpty()){
            throw Exception("Id missing!")
        }
        var pod = podRepository.findById(id.toString())
        if(pod.isPresent){
            return podRepository.save(pod.get());
        }
        throw Exception("Id $id not found!")
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