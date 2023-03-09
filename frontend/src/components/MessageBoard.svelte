<script>
    import Comment from './Comment.svelte'
    import {Button, Chevron, Dropdown, DropdownItem, Helper} from "flowbite-svelte";
    import {afterUpdate, onMount} from "svelte";
    import Error from "./Error.svelte";
    import {page} from '$app/stores';

    let path = $page.url.pathname;

    export let error = null;
    export let review = {
        id: "",
        entry_id: ""
    }
    export let category = {
        id: ""
    }
    let sortedComments = null;
    let amount = 0;
    let order = true;
    let comment = "";

    let show_confirm_deletion_modal = false;

    const handleOrder = (sorting_data) => {
        if (order) return sorting_data.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
        return sorting_data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
    };

    afterUpdate(() => {
        console.log("afterUpdate");
        if (order) {
            scrollToBottom(document.getElementById("CommentSection"))
        } else {
            scrollToTop(document.getElementById("CommentSection"))
        }
    });

    const scrollToBottom = async (node) => {
        node.scroll({top: node.scrollHeight, behavior: 'smooth'});
    };

    const scrollToTop = async (node) => {
        node.scroll({top: 0, behavior: 'smooth'});
    };

    const submitComment = (e) => {
        if (comment.length <= 250) {
            e.preventDefault()
            if (!comment) return;
            let newComment = {
                "content": comment,
                "timestamp": new Date()
            }
            fetch("/api" + path + "/messages", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(newComment)
            })
                .then(resp => resp.json())
                .then(resp => {
                    if (resp.status < 200 || resp.status >= 300) {
                        error = "" + resp.status + ": " + resp.message;
                        console.log(error);
                    } else {
                        loadComments()
                    }
                })
                .catch(err => {
                        console.log(err)
                    }
                )
            comment = ""
        }
    }

    function loadComments() {
        sortedComments = null;
        fetch("/api/categories/" + category.id + "/entries/" + review.entry_id + "/reviews/" + review.id + "/messages")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    sortedComments = handleOrder(resp.content);
                    console.log(sortedComments)
                    amount = sortedComments.length
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadComments()
    })

    $: if (!show_confirm_deletion_modal) {
        loadComments()
    }
</script>

{#if error !== null}
    <Error error={error}/>
{:else}
    {#if (sortedComments === null)}
        LOADING
    {:else}
        <main class="flex grid grid-cols-1 justify-center">
            <header class="flex">
                <h1 class="font-bold text-sm my-2">{amount}{amount > 1 ? " comments" : " comment" }</h1>
            </header>
            <Button color="primary" class="w-48 h-8">
                <Chevron> Jump to {order ? "Newest" : "Oldest"}</Chevron>
            </Button>
            <Dropdown>
                <DropdownItem on:click={()=> {
                    order = !order;
                    handleOrder(sortedComments);
                }}>Jump to {order ? "Oldest" : "Newest"}</DropdownItem>
            </Dropdown>

            <div class="max-h-[34vh] h-screen w-full overflow-y-auto my-4 " id="CommentSection">

                {#each sortedComments as data}
                    <Comment bind:comment={data} category={category} review={review}
                             bind:show_delete={show_confirm_deletion_modal}/>
                {/each}

            </div>
            <form on:submit={submitComment}>
                <input bind:value={comment} class="w-full rounded-lg" id="input-text" placeholder="Enter comment"
                       type="text">
                {#if comment.length >= 250}
                    <Helper class="mt-2 text-red-500" visable={false}><span class="font-medium">Warning!</span> Only
                        Comments under 250 Characters allowed
                    </Helper>
                {/if}
            </form>
        </main>
    {/if}
{/if}
