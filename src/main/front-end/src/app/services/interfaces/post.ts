export interface Post {
  count : number,
  posts : Posts[]
}

export interface Posts{
  postid : string,
  createdDate : string,
  filename : string,
  description : string,
  userid : string,
  username : string
}
