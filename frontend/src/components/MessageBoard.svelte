<script>
    import mock_data from "../mock_data.js";
    import Comment from './Comment.svelte'
    import {
        Chevron,
        Button,
        Dropdown,
        DropdownItem
    } from "flowbite-svelte";
    import {afterUpdate} from "svelte";

    let sortedComments = mock_data.comments
    let amount = sortedComments.length
    let order = true, comment

    const handleOrder = (data) => {
        if(order) return sortedComments = data.sort((a, b) => new Date(a.date) - new Date(b.date))
        return sortedComments = data.sort((a, b) => new Date(b.date) - new Date(a.date))
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
        node.scroll({ top: node.scrollHeight, behavior: 'smooth' });
    };

    const scrollToTop = async (node) => {
        node.scroll({ top: 0, behavior: 'smooth' });
    };

    const submitComment = (e) => {
        e.preventDefault()
        if (!comment) return;
        let comments = sortedComments
        let newComment = {
            "id": comments.length,
            "user": "Kaori Chiriro",
            "content": comment,
            "date": new Date()
        }
        comments = [newComment,...comments]
        sortedComments = handleOrder(comments)
        comment = ""
        amount = sortedComments.length
    }
</script>


<main class="flex grid grid-cols-1 justify-center">
    <header class="flex">
        <h1 class="font-bold text-sm my-2">{amount}{amount > 1 ? " comments" : " comment" }</h1>
    </header>
    <Button class="w-44 h-8"><Chevron> Sort by {order ? "Oldest" : "Newest"}</Chevron></Button>
    <Dropdown>
        <DropdownItem on:click={()=>order = !order} on:click={handleOrder(sortedComments)}>{order ? "Newest" : "Oldest"}</DropdownItem>
    </Dropdown>

    <div id="CommentSection" class="max-h-[34vh] h-screen w-full overflow-y-auto my-4 ">

        {#each sortedComments as data}
            <Comment data={data}/>
        {/each}

    </div>
    <form on:submit={submitComment}>
        <input class="w-full rounded-lg" type="text" id="input-text" placeholder="Enter comment" bind:value={comment}>
    </form>
</main>

