package com.example.kkkezdesy.controller;

import com.example.kkkezdesy.entities.Interest;
import com.example.kkkezdesy.entities.Room;
import com.example.kkkezdesy.entities.User;
import com.example.kkkezdesy.model.RoomEmailRequest;
import com.example.kkkezdesy.repositories.RoomRepo;
import com.example.kkkezdesy.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/room/find")
public class RoomFindController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomRepo roomRepo;

    @GetMapping("/getAllRooms")
    public ResponseEntity<List<Room>> getAllRooms(){
        return new ResponseEntity<List<Room>>(roomRepo.findAll(), HttpStatus.CREATED);
    }

    @PostMapping("/findRoom")
    public ResponseEntity<List<Room>> findRoom(@RequestBody RoomEmailRequest roomEmailRequest){
        User user = userRepo.findByEmail(roomEmailRequest.getEmail());
        if (Objects.equals(roomEmailRequest.getCity(), "")) {
            roomEmailRequest.setCity(user.getCity());
        }

        if(roomEmailRequest.getInterests().size() == 0){
            List<Room> rooms = roomRepo.findByCityContainsAndHeaderContainsAndMinAgeLimitGreaterThanEqualAndMaxAgeLimitLessThanEqualAndMaxMembersLessThanEqual(
                    roomEmailRequest.getCity(), roomEmailRequest.getHeader(), roomEmailRequest.getMinAgeLimit(), roomEmailRequest.getMaxAgeLimit(),
                    roomEmailRequest.getMaxMembers());
            if (roomEmailRequest.getMinAgeLimit() == 0) {
                rooms.removeIf(room -> room.getMinAgeLimit() > user.getAge());
            }
            if (roomEmailRequest.getMaxAgeLimit() == 120) {
                rooms.removeIf(room -> room.getMaxAgeLimit() < user.getAge());
            }
            return new ResponseEntity<List<Room>>(rooms, HttpStatus.CREATED);
        }
        List<Room> rooms2 = roomRepo.findByInterestsMy(
                        setToStringConverter(roomEmailRequest.getInterests()), roomEmailRequest.getInterests().size(),
                        roomEmailRequest.getCity(), roomEmailRequest.getHeader(), roomEmailRequest.getMinAgeLimit(), roomEmailRequest.getMaxAgeLimit(),
                        roomEmailRequest.getMaxMembers()
                );
        if (roomEmailRequest.getMinAgeLimit() == 0) {
            rooms2.removeIf(room -> room.getMinAgeLimit() > user.getAge());
        }
        if (roomEmailRequest.getMaxAgeLimit() == 120) {
            rooms2.removeIf(room -> room.getMaxAgeLimit() < user.getAge());
        }
        return new ResponseEntity<List<Room>>(rooms2, HttpStatus.CREATED);
    }

    public String[] setToStringConverter(Set<Interest> interests){
        Set<String> strings = new HashSet<>();
        for(Interest i : interests){
            strings.add(i.name());
        }

        String[] mArray = new String[strings.size()];
        strings.toArray(mArray);
        return mArray;
    }
}
