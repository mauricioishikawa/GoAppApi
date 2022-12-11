package connect.go.Repositories;

import connect.go.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    List<Driver> findDriverByNameAndLicensePlate(String name, String license);


}
