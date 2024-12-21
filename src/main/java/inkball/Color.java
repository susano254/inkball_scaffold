package inkball;



/**
 * Color class for handling colors
 */
public class Color {
    /**
     * Enum for the color
     */
    public ColorEnum value;


    /**
     * Constructor for the Color class
     * @param value
     */
    public Color(ColorEnum value) {
        this.value = value;
    }

    /**
     * Constructor for the Color class
     * @param value
     */
    public Color(String value) {
        switch(value) {
            case "grey":
                this.value = ColorEnum.GREY;
                break;
            case "blue":
                this.value = ColorEnum.BLUE;
                break;
            case "orange":
                this.value = ColorEnum.ORANGE;
                break;
            case "green":
                this.value = ColorEnum.GREEN;
                break;
            case "yellow":
                this.value = ColorEnum.YELLOW;
                break;
            default:
                this.value = ColorEnum.GREY;
                break;
        }
    }

    /**
     * Constructor for the Color class
     * @param C
     */
    public Color(char C) {
        switch(C) {
            case '0':
                this.value = ColorEnum.GREY;
                break;
            case '1':
                this.value = ColorEnum.ORANGE;
                break;
            case '2':
                this.value = ColorEnum.BLUE;
                break;
            case '3':
                this.value = ColorEnum.GREEN;
                break;
            case '4':
                this.value = ColorEnum.YELLOW;
                break;
            default:
                this.value = ColorEnum.GREY;
                break;
        }
    }

    /**
     * Get the color as a string
     * @return the color as a string
     */
    public String getString(){
        switch(this.value){
            case GREY:
                return "grey";
            case ORANGE:
                return "orange";
            case BLUE:
                return "blue";
            case GREEN:
                return "green";
            case YELLOW:
                return "yellow";
            default:
                return "grey";
        }
    }

    /**
     * Get the color as a char could have used hashmaps but this is more readable
     * @return the number corresponding to the color in the resource folder
     */
    public char getChar(){
        switch(this.value){
            case GREY:
                return '0';
            case ORANGE:
                return '1';
            case BLUE:
                return '2';
            case GREEN:
                return '3';
            case YELLOW:
                return '4';
            default:
                return '0';
        }
    }

    public String toString() {
        return this.value.toString();
    }

}
