package com.license.workguru_app.utils

object Constants {
    //Core
    const val BASE_URL = "https://workguru.webgurus.biz/"
    const val CLIENT_ID = "2"
    const val CLIENT_SECRET = "sc7YwoRAmTsvnNlK8HmjQC4cd2Cb8ddRqIhPIfng"
    const val VUE_APP_USER_AVATAR_URL="https://workguru.webgurus.biz/storage/user-avatar/"

    //authentication
    const val CLIENT_ID_GOOGLE = "866931911552-4tg50kd3edrpbdk97rtvfakjl37vum9t.apps.googleusercontent.com"
    const val LOGIN_URL ="api/login"
    const val REGISTER_URL = "api/register"
    const val FORGOT_PASSWORD_URL = "api/password/forgot"
    const val RESET_PASSWORD_URL = "api/password/reset"
    const val GOOGLE_LOGIN_URL = "api/login/google/id"
    const val GOOGLE_REGISTER_URL = "api/register/google/id"
    const val LOGOUT_URL = "api/logout"
    const val LOGIN_WITH_FACE = "api/login/face"


    //time tracking
    const val GET_PROJECT_URL = "api/projects"
    const val GET_CATEGORIES_URL = "api/categories"
    const val CREATE_TIMER = "api/timers"
    const val PAUSE_TIMER = "api/timers/pause"
    const val SEARCH_BY_KEYWORD = "api/search"
    const val STOP_TIMER = "api/timers/stop"
    const val CREATE_PROJECT = "api/projects"
    const val CREATE_CATEGORY = "api/categories"
    const val UPDATE_PROJECT = "api/projects/"
    const val GET_A_SPECIFIC_TIMER = "api/timers/"


    //profile
    const val GET_USER_PROFILE = "api/users/profile"
    const val GET_STATES = "api/states"
    const val GET_COUNTRIES = "api/countries"
    const val GET_CITIES = "api/cities"
    const val GET_COLLEAGUES = "api/colleagues"
    const val GET_USER_HISTORY = "api/user/history"
    const val CHANGE_USER_PROFILE= "api/users/profile"
    const val STORE_FACE = "api/users/face" //STORE, UPDATE, DELETE


    //help request
    const val GET_PROGRAMMING_LANGUAGE = "api/languages"
    const val GET_STATUSES = "api/statuses"
    const val CHANGE_STATUSES = "api/statuses"
    const val SEND_MESSAGES = "api/messages/" // PLUS ID
    const val GET_MESSAGES_RELATED_TO_USER = "api/messages/" // PLUS ID


    //admin
    const val INVITE_USER_URL = "api/users/invite"
    const val GET_USERS_WHO_ARE_WAITING_FOR_ACCEPTANCE = "api/admin/users"
    const val ACCEPT_USER = "api/admin/users/"
    const val CREATE_NEW_SKILL = "api/languages"


    //Timer
    const val ACTION_STOP = "stop"
    const val ACTION_PAUSE = "pause"
    const val ACTION_RESUME = "resume"
    const val ACTION_START = "start"

    //pusher authorizer
    const val PUSHER_AUTHORIZER_ENDPOINT = "api/broadcasting/auth"
    const val PUSHER_API_KEY = "e4a9156abc59f94e84e6"
    const val PUSHER_CLUSTER = "eu"
}