<script>
    import ConfirmDeletionModal from "./ConfirmDeletionModal.svelte";
    import {Button, Helper, Textarea} from "flowbite-svelte";
    import {onMount} from 'svelte'
    import Cookies from "js-cookie";

    export let comment = null;
    export let error = null;

    export let review;
    export let category;

    let current_user;
    let is_commenter = false;

    let path = ""
    let delete_self_path = ""

    export let show_delete = false;
    let text = comment.content;

    let editable = false;
    let commentedTime = new Date;
    let user = null;

    let current = new Date()
    onMount(() => {
        setTimeout(function () {
            current = new Date();
        }, 60000);
        commentedTime = new Date(Date.parse(comment.timestamp))
    })

    $: calculateDate = (date) => {
        date = new Date(date);

        // To calculate the time difference of two dates
        let diff = current.getTime() - date.getTime();

        // Calculate difference between the date and now
        let diffDay = (diff / (1000 * 3600 * 24))
        let diffHour = (Math.abs(diff) / 36e5)
        let diffMin = (Math.round((diff / 1000) / 60))

        //return the correct string
        if (diffDay < 0) {
            return Math.round(diffDay) + " days ago"
        } else if (diffDay >= 1 && diffDay < 2) {
            return Math.round(diffDay) + " day ago"
        } else if (diffDay > 1) {
            return Math.round(diffDay) + " days ago"
        }
        if (diffHour >= 1 && diffHour < 2) {
            return Math.round(diffHour) + " hour ago"
        } else if (diffHour > 1) {
            return Math.round(diffHour) + " hours ago"
        }
        if (diffMin >= 1 && diffMin < 2) {
            return Math.round(diffMin) + " minute ago"
        } else if (diffMin > 1) {
            return Math.round(diffMin) + " minutes ago"
        }
        return "just now"
    }

    function editComment() {
        if (text.length <= 250) {
            editable = !editable;
            let data = {
                id: comment.id,
                content: text
            }
            fetch("/api" + path + "/messages", {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data),
            })
                .then((response) => response.json())
                .then((response) => {
                    if (response.status < 200 || response.status >= 300) {
                        error = "" + response.status + ": " + response.message;
                        console.log(error);
                    } else {
                        console.log("Save success")
                    }
                })
                .catch(err => console.log(err))
        }
    }

    function loadUser() {
        user = null;
        fetch("/api/users/" + comment.creator_id)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    user = resp
                }
            })
            .catch(err => console.log(err))
    }

    function deleteComment() {
        delete_self_path = path + "/messages/" + comment.id;
        show_delete = true
    }

    let mounted = false
    onMount(() => {
        path = "/categories/" + category.id + "/entries/" + review.entry_id + "/reviews/" + review.id
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
        loadUser()
        mounted = true
    })
    $: if (comment && mounted) {
        is_commenter = (current_user.id === comment.creator_id)
    }
</script>


<div class="rounded-lg outline outline-blue-500 mx-4 my-4 max-w-[90vw] h-fit">
    <div class="h-1"></div>
    {#if comment !== null && user !== null}
        <div class="font-bold flex mx-2 w-full h-fit flex justify-between">
            {user.firstName + " " + user.lastName}
            {#if is_commenter}

                <div class="flex justify-end gap-1 mr-3.5">
                    <Button aria-label="edit_comment" pill size="xs" class="!p-2 w-20 h-7 bg-white" outline on:click={() => editable = true}>
                        Edit
                    </Button>

                    <Button aria-label="delete_comment"  color="red" pill size="xs" class="!p-2 w-20 h-7 bg-white" outline
                            on:click={() => deleteComment()}>
                        Delete
                    </Button>

                </div>
            {/if}
        </div>
        <h1 class="text-xs mx-2 mb-2">{calculateDate(commentedTime)}</h1>
        {#if !editable}
            <Textarea aria-label="comment_text" bind:value={text}
                      class="my-1 font-normal text-gray-700 mx-2 border-none break-all h-fit" type=text disabled/>
        {:else }
            <Textarea bind:value={text}
                      class="my-1 font-normal text-gray-700 mx-2 rounded-lg relative w-[98%]" type=text
                      on:keydown={() => {if (event.keyCode === 13){ editComment() }}}/>
            {#if text.length >= 250}
                <Helper class="mt-2 text-red-500" visable={false}><span class="font-medium">Warning!</span> Only
                    Comments under 250 Characters allowed
                </Helper>
            {/if}
        {/if}
    {:else }
        LOADING COMMENT
    {/if}
</div>

{#if delete_self_path !== ""}
    <ConfirmDeletionModal hide="{() => show_delete = false}" show="{show_delete}"
                          to_delete={delete_self_path} delete_name="comment" afterpath={path}/>
{/if}