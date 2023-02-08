<script>
    import {ChevronLeft, ChevronRight, Pagination} from "flowbite-svelte";

    export let previous;
    export let next;
    export let currentPage;
    export let lastPage;
    export let click = () => {
    };

    const pages = (numberOfPages, currentPage, lastPage) => {
        return [...Array(2 * numberOfPages).keys()]
            .map(i => currentPage - numberOfPages + i)
            .filter(i => i > 0 && i <= lastPage)
            .slice(0, numberOfPages)
            .map(page => ({
                name: page,
                active: page === currentPage,
            }));
    };
</script>

<div class="mx-auto m-8">
    <Pagination icon on:click={click} on:next={next} on:previous={previous} pages={pages(5, currentPage, lastPage)}>
        <svelte:fragment slot="prev">
            <span class="sr-only">Previous</span>
            <ChevronLeft class="w-5 h-5"/>
        </svelte:fragment>
        <svelte:fragment slot="next">
            <span class="sr-only">Next</span>
            <ChevronRight class="w-5 h-5"/>
        </svelte:fragment>
    </Pagination>
</div>