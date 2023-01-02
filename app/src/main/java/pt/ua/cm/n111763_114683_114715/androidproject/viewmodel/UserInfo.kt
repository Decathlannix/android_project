package pt.ua.cm.n111763_114683_114715.androidproject.viewmodel

class UserInfo(userID: String, userName: String, userEmail: String, userScore: Int, userCountry: String, userImage: String) {
    private var _uid: String
    val uid: String
        get() = _uid

    private var _name: String
    val name: String
        get() = _name

    private var _email: String
    val email: String
        get() = _email

    private var _image: String
    val image: String
        get() = _image

    private var _country: String
    val country: String
        get() = _country

    private var _score: Int
    val score: Int
        get() = _score

    init {
        _uid = userID
        _name = userName
        _email = userEmail
        _score = userScore
        _image = userImage
        _country = userCountry
    }

    fun setImage(imagePath: String) {
        _image = imagePath
    }
}