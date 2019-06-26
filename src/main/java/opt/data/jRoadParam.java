package opt.data;

import java.util.Objects;

public class jRoadParam {
    long id = 0;
    float capacity;
    float speed;
    float jam_density;

    public jRoadParam(float capacity, float speed, float jam_density) {
        this.capacity = capacity;
        this.speed = speed;
        this.jam_density = jam_density;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        jRoadParam that = (jRoadParam) o;
        return Float.compare(that.capacity, capacity) == 0 &&
                Float.compare(that.speed, speed) == 0 &&
                Float.compare(that.jam_density, jam_density) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, speed, jam_density);
    }

}
