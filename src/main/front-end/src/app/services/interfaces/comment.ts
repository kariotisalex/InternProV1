export interface Comment {
  count : number,
  comments: Comments[];

}

export interface Comments{
  commentid : string,
  createdate : string,
  comment : string,
  userid : string,
  username  : string,
  postid : string
}
