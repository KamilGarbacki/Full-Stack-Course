const UserProfile = ({name, age, gender, imageNumber, ...props}) => {

    gender = gender === "MALE" ? "men" : "women"

    return (
        <div>
            <p>{name}</p>
            <p>{age}</p>
            <img 
                src = {`https://randomuser.me/api/portraits/${gender}/${imageNumber}.jpg`}
                alt="" 
            />
            {props.children}
        </div>
    )
}

export default UserProfile

const userProfile = (name, age, gander) =>{

}