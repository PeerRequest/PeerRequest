<script>
    import Comment from './Comment.svelte'
    import {Button, Chevron, Dropdown, DropdownItem} from "flowbite-svelte";
    import {afterUpdate, onMount} from "svelte";
    import Error from "./Error.svelte";


    export let error = null;
    export let review = {
        id: "",
        entry_id:""
    }
    export let category = {
        id: ""
    }
    let sortedComments = null
    let amount = 0
    let order = true, comment




    const handleOrder = (sorting_data) => {
        if (order) return sortedComments = sorting_data.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
        return sortedComments = sorting_data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
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
        e.preventDefault()
        if (!comment) return;
        let comments = sortedComments
        let newComment = {
            "id": comments.length,
            //TODO : Usercontoller
            "user": "Kaori Chiriro",
            "content": comment,
            "timestamp": new Date()
        }
        comments = [newComment, ...comments]
        sortedComments = handleOrder(comments)
        comment = ""
        amount = sortedComments.length
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
                    sortedComments = resp.content;
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
        loadComments()
    })
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
            <Button class="w-44 h-8">
                <Chevron> Sort by {order ? "Oldest" : "Newest"}</Chevron>
            </Button>
            <Dropdown>
                <DropdownItem on:click={()=>order = !order}
                              on:click={handleOrder(sortedComments)}>{order ? "Newest" : "Oldest"}</DropdownItem>
            </Dropdown>

            <div class="max-h-[34vh] h-screen w-full overflow-y-auto my-4 " id="CommentSection">

                {#each sortedComments as data}
                    <Comment bind:data={data}/>
                {/each}

            </div>
            <form on:submit={submitComment}>
                <input bind:value={comment} class="w-full rounded-lg" id="input-text" placeholder="Enter comment" type="text">
            </form>
        </main>
    {/if}
{/if}
