package hudson.tasks.JavadocArchiver;

f=namespace(lib.FormTagLib)

f.entry(title:_("Javadoc directory"), description:_("description"), field:"javadocDir") {
    f.textbox()
}
f.entry(field:"keepAll") {
    f.checkbox(title:_("Retain Javadoc for each successful build"))
}
