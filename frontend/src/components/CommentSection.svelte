<script>
    import {store} from '../store/store'
    import Comment from './Comment.svelte'
    import {
        Chevron,
        Button,
        Dropdown,
        DropdownItem
    } from "flowbite-svelte";


    let order = true, comment
    $:  sortedComments = $store[0].comments

    const handleOrder = (data) => {
        if(!order) return sortedComments = data.sort((a, b) => new Date(a.date) - new Date(b.date))
        return sortedComments = data.sort((a, b) => new Date(b.date) - new Date(a.date))
    };

    const submitComment = (e) => {
        e.preventDefault()
        if (!comment) return;
        let comments = $store[0].comments
        let newComment = {
            "id": comments.length,
            "user": $store[0].user.name,
            "content": comment,
            "date": new Date()
        }
        $store[0].comments = [newComment,...comments]
        sortedComments = handleOrder($store[0].comments)
        comment = ""
    }
</script>



<main>
    <div class="grid gap-y-6 justify-center">
        <Button class="w-44"><Chevron> Sort by {order ? "Oldest" : "Newest"}</Chevron></Button>
        <Dropdown>
            <DropdownItem on:click={()=>order = !order} on:click={handleOrder(sortedComments)}>{order ? "Newest" : "Oldest"}</DropdownItem>
        </Dropdown>

        <div class="max-h-64 overflow-y-auto w-full">

            {#each sortedComments as data}
                <Comment data={data}/>
            {/each}

        </div>
        <form on:submit={submitComment}>
            <input class="w-full rounded-lg" type="text" id="input-text" placeholder="Enter comment" bind:value={comment}>
        </form>
    </div>

</main>

