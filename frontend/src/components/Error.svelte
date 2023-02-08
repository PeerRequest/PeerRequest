<script>
    import {Button} from "flowbite-svelte";

    export let header = "Network Error";
    export let error;
</script>

<div class="container mx-auto mt-8" role="alert">
    <div class="bg-red-500 text-white font-bold rounded-t px-4 py-2">
        {header}
    </div>
    <div class="border border-t-0 border-red-400 rounded-b bg-red-100 px-4 py-3 text-red-700">
        <div class="flex">
            <Button color="red" on:click={() => location.reload()} outline size="sm">Reload</Button>
            <p class="my-auto ml-4">{error}</p>
        </div>
    </div>
</div>