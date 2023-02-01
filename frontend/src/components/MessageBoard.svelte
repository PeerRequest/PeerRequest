<script>
    import {store} from '../store/store'
    import CommentSection from "./CommentSection.svelte";
    $: amount = $store[0].comments.length


</script>


<main>
    <header class="flex">
        <h1 class="font-bold ">{amount}{amount > 1 ? " comments" : " comment" }</h1>
    </header>
    <CommentSection/>
</main>

