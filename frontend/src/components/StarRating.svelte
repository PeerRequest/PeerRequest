<script>

    let numberOfStars = 5;
    export let selectionArray = Array(numberOfStars);

    let selected = "w-5 h-5 text-yellow-400"
    let unselected = "w-5 h-5 text-gray-300 dark:text-gray-500"

    for (let i = 0; i < selectionArray.length; i++) {
        selectionArray[i]={unselected};
    }

    let fill = "currentColor"
    let viewBox = "0 0 20 20"
    let xmlns = "http://www.w3.org/2000/svg"
    let d = "M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 " +
        "1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175" +
        " 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1." +
        "81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"

    let i = 0;
</script>


<div class="flex items-center">
    {#each selectionArray as i}
        <svg aria-hidden="true"
             class={selectionArray[i]}
             fill={fill}
             viewBox={viewBox}
             xmlns="http://www.w3.org/2000/svg">
            <title>Star number {i+1}</title>
            <path d={d}></path>
        </svg>
    {/each}
</div>
